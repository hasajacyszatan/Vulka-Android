package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.medzik.android.utils.runOnIOThread
import io.github.vulka.business.crypto.decryptCredentials
import io.github.vulka.business.utils.getUserClient
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExamsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val date = LocalDate.now()
    var dateFrom = MutableStateFlow(date.with(
        TemporalAdjusters.previousOrSame(
            DayOfWeek.MONDAY)))
    var dateTo = MutableStateFlow(date.with(
        TemporalAdjusters.nextOrSame(
            DayOfWeek.SUNDAY)))

    private var client by mutableStateOf<UserClient?>(null)
    private var userClientCredentialsRenewed by mutableStateOf(false)
    private var student by mutableStateOf<Student?>(null)

    var examRefreshing = MutableStateFlow(false)

    val exception = MutableStateFlow<Exception?>(null)
    var loadingError = MutableStateFlow(false)

    var userId by mutableStateOf<UUID?>(null)

    val exams = MutableStateFlow<List<io.github.vulka.database.Exams>>(emptyList())

    suspend fun init(args: Exams) = viewModelScope.launch(Dispatchers.IO) {
        client = getUserClientFromCredentials(args.platform, args.credentials)
        student = getStudentFromCredentials(UUID.fromString(args.userId))

        userId = UUID.fromString(args.userId)

        updateExams()
    }

    suspend fun updateExams() {
        if (userId != null) {
            val examsState = repository.exams.getRangeByCredentialsId(dateFrom.value,dateTo.value,userId!!).first()
            exams.value = examsState.sortedBy { it.exam.deadline }
        }
    }

    private suspend fun checkIfExamsShouldBeSync(dateFrom: LocalDate, dateTo: LocalDate,userId: UUID): Boolean {
        val exams = repository.exams.getRangeByCredentialsId(dateFrom,dateTo,userId).first()

        if (exams.isNotEmpty()) {
            val lesson = exams[0]

            val duration = Duration.between(lesson.lastSync, LocalDateTime.now())
            return duration.toMinutes() >= 15
        } else
            return true
    }

    private suspend fun syncExamsAtSwitch(client: UserClient, student: Student, dateFrom: LocalDate, dateTo: LocalDate, userId: UUID) {
        val now = LocalDateTime.now()

        val exams = client.getExam(student, dateFrom, dateTo)
        repository.exams.deleteRangeByCredentialsId(dateFrom, dateTo, userId)
        for (exam in exams) {
            repository.exams.insert(
                io.github.vulka.database.Exams(
                    exam = exam,
                    lastSync = now,
                    credentialsId = userId
                )
            )
        }
    }

    private suspend fun syncExams() {
        if (!checkIfExamsShouldBeSync(dateFrom.value, dateTo.value, userId!!)) {
            updateExams()
            return
        }

        if (!userClientCredentialsRenewed) {
            client?.renewCredentials()
            userClientCredentialsRenewed = true
        }

        examRefreshing.value = true

        try {
            loadingError.value = false
            syncExamsAtSwitch(client!!,student!!,dateFrom.value,dateTo.value, userId!!)

            updateExams()
        } catch (e: Exception) {
            loadingError.value = true
            exception.value = e
        }

        examRefreshing.value = false
    }

    fun onForward() {
        dateFrom.value = nextWeek(dateFrom.value)
        dateTo.value = nextWeek(dateTo.value)

        runOnIOThread {
            syncExams()
        }
    }

    fun onBack() {
        dateFrom.value = previousWeek(dateFrom.value)
        dateTo.value = previousWeek(dateTo.value)

        runOnIOThread {
            syncExams()
        }
    }

    private fun nextWeek(date: LocalDate): LocalDate {
        return date.plusWeeks(1)
    }

    private fun previousWeek(date: LocalDate): LocalDate {
        return date.minusWeeks(1)
    }

    private fun getUserClientFromCredentials(platform: Platform, credentials: String): UserClient {
        val decryptedCredentials = decryptCredentials(credentials)
        val client = getUserClient(platform, decryptedCredentials)
        return client
    }

    private suspend fun getStudentFromCredentials(userId: UUID): Student {
        val dbCredentials = repository.credentials.getById(userId).first()!!
        return dbCredentials.student
    }
}

