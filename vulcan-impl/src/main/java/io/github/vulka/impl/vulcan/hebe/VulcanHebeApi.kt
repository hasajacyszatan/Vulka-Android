package io.github.vulka.impl.vulcan.hebe

import io.github.vulka.impl.vulcan.Utils
import io.github.vulka.impl.vulcan.VulcanLoginCredentials
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.hebe.login.RegisterRequest
import io.github.vulka.impl.vulcan.hebe.types.HebeAccount
import io.github.vulka.impl.vulcan.hebe.types.HebeAverageGrade
import io.github.vulka.impl.vulcan.hebe.types.HebeChangedLesson
import io.github.vulka.impl.vulcan.hebe.types.HebeGrade
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
import java.util.*

class VulcanHebeApi {
    private lateinit var client: HebeHttpClient
    private lateinit var credentials: VulcanLoginCredentials

    fun setup(credentials: VulcanLoginCredentials) {
        client = HebeHttpClient(credentials.keystore)
        this.credentials = credentials
    }

    private fun getBaseUrl(token: String): String = runBlocking {
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

        val response = client.post(fullUrl, registerRequest,HebeAccount::class.java)

        credentials = VulcanLoginCredentials(response!!,keystore)
        return response
    }

    fun getStudents(): Array<HebeStudent> {
        val baseUrl = credentials.account.restUrl

        val fullUrl = "$baseUrl${HebeApiEndpoints.STUDENT_LIST}"

        return client.get(fullUrl, Array<HebeStudent>::class.java)!!
    }

    private fun <T> getByPupil(
        endpoint: String,
        student: HebeStudent,
        period: HebePeriod? = null,
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
        clazz: Class<T>,
        query: Map<String,String>? = null
    ) = runBlocking {
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

        return@runBlocking client.get(
            url = "$baseUrl/${HebeApiEndpoints.DATA_ROOT}/${endpoint}/${HebeApiEndpoints.DATA_BY_PUPIL}",
            query = queryMap,
            clazz = clazz
        )!!
    }

    fun getLuckyNumber(student: HebeStudent,date: LocalDate): Int {
        val baseUrl = getRestUrl(student)
        val response = client.get(
            url = "$baseUrl/${HebeApiEndpoints.DATA_ROOT}/${HebeApiEndpoints.DATA_LUCKY_NUMBER}",
            query = mapOf(
                "constituentId" to student.school.id.toString(),
                "day" to DateTimeFormatter.ISO_LOCAL_DATE.format(date)
            ),
            clazz = HebeLuckyNumber::class.java
        )
        return response!!.number
    }

    fun getGrades(student: HebeStudent,period: HebePeriod): Array<HebeGrade> {
        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_GRADE,
            student = student,
            period = period,
            clazz = Array<HebeGrade>::class.java
        )
    }

    fun getLessons(student: HebeStudent,dateFrom: LocalDate,dateTo: LocalDate = dateFrom): Array<HebeLesson> {
        val currentPeriod = student.periods.find { it.current }!!

        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_TIMETABLE,
            student = student,
            period = currentPeriod,
            dateFrom = dateFrom,
            dateTo = dateTo,
            clazz = Array<HebeLesson>::class.java,
        )
    }

    fun getChangedLessons(student: HebeStudent,dateFrom: LocalDate,dateTo: LocalDate = dateFrom): Array<HebeChangedLesson> {
        val currentPeriod = student.periods.find { it.current }!!

        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_TIMETABLE_CHANGES,
            student = student,
            period = currentPeriod,
            dateFrom = dateFrom,
            dateTo = dateTo,
            clazz = Array<HebeChangedLesson>::class.java,
        )
    }

    fun getSummaryGrades(student: HebeStudent, period: HebePeriod): Array<HebeSummaryGrade> {
        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_GRADE_SUMMARY,
            student = student,
            period = period,
            clazz = Array<HebeSummaryGrade>::class.java
        )
    }

    fun getAverages(student: HebeStudent, period: HebePeriod): Array<HebeAverageGrade> {
        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_GRADE_AVERAGE,
            student = student,
            period = period,
            clazz = Array<HebeAverageGrade>::class.java
        )
    }

    fun getNotes(student: HebeStudent): Array<HebeNote> {
        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_NOTES,
            student = student,
            clazz = Array<HebeNote>::class.java
        )
    }

    fun getMeetings(student: HebeStudent, dateFrom: LocalDate): Array<HebeMeeting> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return getByPupil(
            endpoint = HebeApiEndpoints.DATA_MEETINGS,
            student = student,
            clazz = Array<HebeMeeting>::class.java,

            query = mapOf(
                "from" to dateFormatter.format(dateFrom)
            )
        )
    }
}
