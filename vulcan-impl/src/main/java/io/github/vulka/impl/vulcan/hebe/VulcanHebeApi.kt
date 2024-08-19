package io.github.vulka.impl.vulcan.hebe

import io.github.vulka.impl.vulcan.Utils
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.hebe.login.RegisterRequest
import io.github.vulka.impl.vulcan.hebe.types.HebeAccount
import io.github.vulka.impl.vulcan.hebe.types.HebeAverageGrade
import io.github.vulka.impl.vulcan.hebe.types.HebeChangedLesson
import io.github.vulka.impl.vulcan.hebe.types.HebeExam
import io.github.vulka.impl.vulcan.hebe.types.HebeGrade
import io.github.vulka.impl.vulcan.hebe.types.HebeHomework
import io.github.vulka.impl.vulcan.hebe.types.HebeLesson
import io.github.vulka.impl.vulcan.hebe.types.HebeLuckyNumber
import io.github.vulka.impl.vulcan.hebe.types.HebeMeeting
import io.github.vulka.impl.vulcan.hebe.types.HebeNote
import io.github.vulka.impl.vulcan.hebe.types.HebePeriod
import io.github.vulka.impl.vulcan.hebe.types.HebeStudent
import io.github.vulka.impl.vulcan.hebe.types.HebeSummaryGrade
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

open class VulcanHebeApi {
    protected lateinit var client: HebeHttpClient
    protected lateinit var credentials: VulcanHebeLoginCredentials

    open fun setup(credentials: VulcanHebeLoginCredentials) {
        client = HebeHttpClient(credentials.keystore)
        this.credentials = credentials
    }

    protected open fun getBaseUrl(token: String): String = runBlocking {
        val client = HttpClient(OkHttp)

        val response = client.get("http://komponenty.vulcan.net.pl/UonetPlusMobile/RoutingRules.txt")

        if (response.status.value !in 200..299)
            throw IOException("Unexpected code $response")

        val content = response.bodyAsText()
        val lines = content.lines()

        for (line in lines) {
            if (line.startsWith(token.substring(0,3)))
                return@runBlocking line.substring(line.indexOf(",") + 1)
        }

        throw InvalidTokenException("Invalid token prefix")
    }

    private fun getRestUrl(student: HebeStudent): String {
        return credentials.account.restUrl + student.unit.code
    }

    fun register(keystore: HebeKeystore, symbol: String, token: String, pin: String): HebeAccount {
        val upperToken = token.uppercase()
        val lowerSymbol = symbol.lowercase()

        client = HebeHttpClient(keystore)

        val baseUrl = getBaseUrl(upperToken)

        val fullUrl = "$baseUrl/$lowerSymbol/${HebeApiEndpoints.DEVICE_REGISTER}"

        val (certificate,fingerprint,_) = keystore.getData()

        val registerRequest = RegisterRequest(
            os = "Android",
            deviceModel = keystore.deviceModel,
            certificate = certificate,
            certificateType = "X509",
            certificateThumbprint = fingerprint,
            pin = pin,
            securityToken = upperToken,
            selfIdentifier = Utils.uuid(fingerprint)
        )

        val response = client.post<RegisterRequest,HebeAccount>(fullUrl, registerRequest)

        credentials = VulcanHebeLoginCredentials(response!!,keystore)
        return response
    }

    fun getStudents(): Array<HebeStudent> {
        val baseUrl = credentials.account.restUrl

        val fullUrl = "$baseUrl${HebeApiEndpoints.STUDENT_LIST}"

        return client.get<Array<HebeStudent>>(fullUrl)!!
    }

    private inline fun <reified T> getByPupil(
        endpoint: String,
        student: HebeStudent,
        period: HebePeriod? = null,
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
        query: Map<String,String>? = null
    ): T {
        val baseUrl = getRestUrl(student)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val queryMap = mutableMapOf(
            "unitId" to student.unit.id.toString(),
            "pupilId" to student.pupil.id.toString(),
        )

        if (period != null)
            queryMap["periodId"] = period.id.toString()
        if (dateFrom != null)
            queryMap["dateFrom"] = dateFrom.format(dateFormatter)
        if (dateTo != null)
            queryMap["dateTo"] = dateTo.format(dateFormatter)

        queryMap["lastId"] = "-2147483648"  // don't ask, it's just Vulcan
        queryMap["pageSize"] = 500.toString()

        query?.forEach { (key, value) -> queryMap[key] = value }

        return client.get<T>(
            url = "$baseUrl/${HebeApiEndpoints.DATA_ROOT}/${endpoint}/${HebeApiEndpoints.DATA_BY_PUPIL}",
            query = queryMap,
        )!!
    }

    fun getLuckyNumber(student: HebeStudent, date: LocalDate): Int {
        val baseUrl = getRestUrl(student)
        val response = client.get<HebeLuckyNumber>(
            url = "$baseUrl/${HebeApiEndpoints.DATA_ROOT}/${HebeApiEndpoints.DATA_LUCKY_NUMBER}",
            query = mapOf(
                "constituentId" to student.school.id.toString(),
                "day" to DateTimeFormatter.ISO_LOCAL_DATE.format(date)
            ),
        )
        return response!!.number
    }

    fun getGrades(student: HebeStudent,period: HebePeriod): Array<HebeGrade> {
        return getByPupil<Array<HebeGrade>>(
            endpoint = HebeApiEndpoints.DATA_GRADE,
            student = student,
            period = period,
        )
    }

    fun getLessons(student: HebeStudent,dateFrom: LocalDate,dateTo: LocalDate = dateFrom): Array<HebeLesson> {
        val currentPeriod = student.periods.find { it.current }!!

        return getByPupil<Array<HebeLesson>>(
            endpoint = HebeApiEndpoints.DATA_TIMETABLE,
            student = student,
            period = currentPeriod,
            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getChangedLessons(student: HebeStudent,dateFrom: LocalDate, dateTo: LocalDate = dateFrom): Array<HebeChangedLesson> {
        val currentPeriod = student.periods.find { it.current }!!

        return getByPupil<Array<HebeChangedLesson>>(
            endpoint = HebeApiEndpoints.DATA_TIMETABLE_CHANGES,
            student = student,
            period = currentPeriod,
            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getSummaryGrades(student: HebeStudent, period: HebePeriod): Array<HebeSummaryGrade> {
        return getByPupil<Array<HebeSummaryGrade>>(
            endpoint = HebeApiEndpoints.DATA_GRADE_SUMMARY,
            student = student,
            period = period,
        )
    }

    fun getAverages(student: HebeStudent, period: HebePeriod): Array<HebeAverageGrade> {
        return getByPupil<Array<HebeAverageGrade>>(
            endpoint = HebeApiEndpoints.DATA_GRADE_AVERAGE,
            student = student,
            period = period,

            query = mapOf(
                "scope" to "auto"
            )
        )
    }

    fun getNotes(student: HebeStudent): Array<HebeNote> {
        return getByPupil<Array<HebeNote>>(
            endpoint = HebeApiEndpoints.DATA_NOTES,
            student = student,
        )
    }

    fun getMeetings(student: HebeStudent, dateFrom: LocalDate): Array<HebeMeeting> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return getByPupil<Array<HebeMeeting>>(
            endpoint = HebeApiEndpoints.DATA_MEETINGS,
            student = student,

            query = mapOf(
                "from" to dateFormatter.format(dateFrom)
            )
        )
    }

    fun getHomeworks(student: HebeStudent,dateFrom: LocalDate,dateTo: LocalDate = dateFrom): Array<HebeHomework> {
        return getByPupil<Array<HebeHomework>>(
            endpoint = HebeApiEndpoints.DATA_HOMEWORK,
            student = student,

            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getExams(student: HebeStudent,dateFrom: LocalDate,dateTo: LocalDate = dateFrom): Array<HebeExam>  {
        return getByPupil<Array<HebeExam>>(
            endpoint = HebeApiEndpoints.DATA_EXAM,
            student = student,

            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }
}
