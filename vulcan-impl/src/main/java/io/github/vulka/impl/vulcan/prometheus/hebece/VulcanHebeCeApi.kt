package io.github.vulka.impl.vulcan.prometheus.hebece

import com.fleeksoft.ksoup.Ksoup
import io.github.vulka.impl.vulcan.Utils
import io.github.vulka.impl.vulcan.hebe.HebeHttpClient
import io.github.vulka.impl.vulcan.hebe.VulcanHebeLoginCredentials
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.hebe.types.*
import io.github.vulka.impl.vulcan.prometheus.PrometheusNoStudentsException
import io.github.vulka.impl.vulcan.prometheus.VulcanPrometheusLoginCredentials
import io.github.vulka.impl.vulcan.prometheus.decodeJWT
import io.github.vulka.impl.vulcan.prometheus.hebece.login.RegisterRequest
import io.github.vulka.impl.vulcan.prometheus.hebece.types.HebeCeChangedLesson
import io.github.vulka.impl.vulcan.prometheus.hebece.types.HebeCeStudent
import io.github.vulka.impl.vulcan.prometheus.login.ApiApResponse
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class VulcanHebeCeApi {
    private lateinit var prometheusCredentials: VulcanPrometheusLoginCredentials

    private val hebeHttpClients: MutableMap<String,HebeCeHttpClient> = mutableMapOf()
    private lateinit var hebeLoginCredentials: Map<String,VulcanHebeLoginCredentials>

    fun setup(credentials: VulcanPrometheusLoginCredentials) {
        this.prometheusCredentials = credentials
    }

    suspend fun renewCredentials() {
        val httpClient = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }

        val apiApPage = httpClient.get("https://eduvulcan.pl/api/ap") {
            contentType(ContentType.Application.Json)
            headers {
                append("user-agent",HebeHttpClient.APP_USER_AGENT)
                append("vapi","1")
                append("vcanonicalurl","api%2fap")
                append("vos",HebeHttpClient.APP_OS)
                append("vversioncode","612")
                append("vdate",ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                append("Authorization","Bearer ${prometheusCredentials.accessToken}")
            }
        }.bodyAsText()

        val apiApResponse = Json.decodeFromString<ApiApResponse>(
            Ksoup.parse(apiApPage)
            .select("#ap").attr("value"))

        if (apiApResponse.tokens.isEmpty())
            throw PrometheusNoStudentsException()

        val tokens = apiApResponse.tokens.map { decodeJWT(it) }
        val symbols = tokens.distinctBy { it.tenant }.map { it.tenant }

        hebeLoginCredentials = symbols.associateWith { symbol ->
            val keystore = HebeKeystore.create(HebeKeystore.generateKeystoreName(symbol),"",prometheusCredentials.deviceModel)
            val hebeHttpClient = HebeCeHttpClient(keystore)
            hebeHttpClients[symbol] = hebeHttpClient
            VulcanHebeLoginCredentials(
                keystore = keystore,
                account = register(
                    httpClient = hebeHttpClient,
                    keystore = keystore,
                    symbol = symbol,
                    tokens = apiApResponse.tokens.toTypedArray()
                )
            )
        }
    }

    private fun getBaseUrl(): String {
        // TODO: Reverse engineer how new routing works
        return "https://lekcjaplus.vulcan.net.pl"
    }

    private fun getCredentials(student: HebeCeStudent): VulcanHebeLoginCredentials {
        return hebeLoginCredentials[student.symbol]!!
    }

    private fun getHttpClient(symbol: String): HebeCeHttpClient {
        return hebeHttpClients[symbol]!!
    }

    private fun getHttpClient(student: HebeCeStudent): HebeCeHttpClient {
        return getHttpClient(student.symbol)
    }

    private fun getRestUrl(student: HebeCeStudent): String {
        return getCredentials(student).account.restUrl + student.unit.code
    }

    private fun register(httpClient: HebeCeHttpClient,keystore: HebeKeystore, symbol: String, tokens: Array<String>): HebeAccount {
        val baseUrl = getBaseUrl()

        val fullUrl = "$baseUrl/$symbol/${HebeCeApiEndpoints.DEVICE_REGISTER}"

        val (certificate,fingerprint,_) = keystore.getData()

        val registerRequest = RegisterRequest(
            os = "Android",
            deviceModel = keystore.deviceModel,
            certificate = certificate,
            certificateType = "X509",
            certificateThumbprint = fingerprint,
            selfIdentifier = Utils.uuid(fingerprint),
            tokens = tokens
        )

        val response = httpClient.post<RegisterRequest, HebeAccount>(
            url = fullUrl,
            body = registerRequest,
        )

        return response!!
    }

    fun getStudents(): Array<HebeCeStudent> {
        val students = ArrayList<HebeCeStudent>()

        hebeLoginCredentials.forEach { (symbol,credential) ->
            val baseUrl = credential.account.restUrl

            val fullUrl = "$baseUrl${HebeCeApiEndpoints.STUDENT_LIST}"

            getHttpClient(symbol).get<Array<HebeCeStudent>>(
                url = fullUrl,
                query = mapOf(
                    "mode" to "2"
                )
            )!!.forEach {
                students.add(it)
            }
        }

        return students.toTypedArray()
    }

    private inline fun <reified T> getByPupil(
        endpoint: String,
        student: HebeCeStudent,
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

        return getHttpClient(student).get<T>(
            url = "$baseUrl/${HebeCeApiEndpoints.DATA_ROOT}/${endpoint}/${HebeCeApiEndpoints.DATA_BY_PUPIL}",
            query = queryMap,
        )!!
    }

    fun getLuckyNumber(student: HebeCeStudent, date: LocalDate): Int {
        val baseUrl = getRestUrl(student)
        val response = getHttpClient(student).get<HebeLuckyNumber>(
            url = "$baseUrl/${HebeCeApiEndpoints.DATA_ROOT}/${HebeCeApiEndpoints.DATA_LUCKY_NUMBER}",
            query = mapOf(
                "constituentId" to student.school.id.toString(),
                "day" to DateTimeFormatter.ISO_LOCAL_DATE.format(date)
            ),
        )
        return response!!.number
    }

    fun getGrades(student: HebeCeStudent, period: HebePeriod): Array<HebeGrade> {
        return getByPupil<Array<HebeGrade>>(
            endpoint = HebeCeApiEndpoints.DATA_GRADE,
            student = student,
            period = period,
        )
    }

    fun getLessons(student: HebeCeStudent, dateFrom: LocalDate, dateTo: LocalDate = dateFrom): Array<HebeLesson> {
        val currentPeriod = student.periods.find { it.current }!!

        return getByPupil<Array<HebeLesson>>(
            endpoint = HebeCeApiEndpoints.DATA_TIMETABLE,
            student = student,
            period = currentPeriod,
            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getChangedLessons(student: HebeCeStudent, dateFrom: LocalDate, dateTo: LocalDate = dateFrom): Array<HebeCeChangedLesson> {
        val currentPeriod = student.periods.find { it.current }!!

        return getByPupil<Array<HebeCeChangedLesson>>(
            endpoint = HebeCeApiEndpoints.DATA_TIMETABLE_CHANGES,
            student = student,
            period = currentPeriod,
            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getSummaryGrades(student: HebeCeStudent, period: HebePeriod): Array<HebeSummaryGrade> {
        return getByPupil<Array<HebeSummaryGrade>>(
            endpoint = HebeCeApiEndpoints.DATA_GRADE_SUMMARY,
            student = student,
            period = period,
        )
    }

    fun getAverages(student: HebeCeStudent, period: HebePeriod): Array<HebeAverageGrade> {
        return getByPupil<Array<HebeAverageGrade>>(
            endpoint = HebeCeApiEndpoints.DATA_GRADE_AVERAGE,
            student = student,
            period = period,

            query = mapOf(
                "scope" to "auto"
            )
        )
    }

    fun getNotes(student: HebeCeStudent): Array<HebeNote> {
        return getByPupil<Array<HebeNote>>(
            endpoint = HebeCeApiEndpoints.DATA_NOTES,
            student = student,
        )
    }

    fun getMeetings(student: HebeCeStudent, dateFrom: LocalDate): Array<HebeMeeting> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return getByPupil<Array<HebeMeeting>>(
            endpoint = HebeCeApiEndpoints.DATA_MEETINGS,
            student = student,

            query = mapOf(
                "from" to dateFormatter.format(dateFrom)
            )
        )
    }

    fun getHomeworks(student: HebeCeStudent, dateFrom: LocalDate, dateTo: LocalDate = dateFrom): Array<HebeHomework> {
        return getByPupil<Array<HebeHomework>>(
            endpoint = HebeCeApiEndpoints.DATA_HOMEWORK,
            student = student,

            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getExams(student: HebeCeStudent, dateFrom: LocalDate, dateTo: LocalDate = dateFrom): Array<HebeExam>  {
        return getByPupil<Array<HebeExam>>(
            endpoint = HebeCeApiEndpoints.DATA_EXAM,
            student = student,

            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }

    fun getAttendance(student: HebeCeStudent, dateFrom: LocalDate, dateTo: LocalDate = dateFrom): Array<HebeAttendance> {
        return getByPupil<Array<HebeAttendance>>(
            endpoint = HebeCeApiEndpoints.DATA_ATTENDANCE,
            student = student,

            dateFrom = dateFrom,
            dateTo = dateTo,
        )
    }
}