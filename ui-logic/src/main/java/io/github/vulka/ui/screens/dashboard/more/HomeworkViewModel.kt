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
import io.github.vulka.database.entities.Homeworks
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
class HomeworkViewModel @Inject constructor(
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

    var homeworkRefreshing = MutableStateFlow(false)

    val exception = MutableStateFlow<Exception?>(null)
    var loadingError = MutableStateFlow(false)

    var userId by mutableStateOf<UUID?>(null)

    val homeworks = MutableStateFlow<List<Homeworks>>(emptyList())

    suspend fun init(args: Homework) = viewModelScope.launch(Dispatchers.IO) {
        client = getUserClientFromCredentials(args.platform, args.credentials)
        student = getStudentFromCredentials(UUID.fromString(args.userId))

        userId = UUID.fromString(args.userId)

        updateHomeworks()
    }

    suspend fun updateHomeworks() {
        if (userId != null) {
            val homeworksState = repository.homeworks.getRangeByCredentialsId(dateFrom.value,dateTo.value,userId!!).first()
            homeworks.value = homeworksState.sortedBy { it.homework.deadline }
        }
    }

    private suspend fun checkIfHomeworksShouldBeSync(dateFrom: LocalDate, dateTo: LocalDate,userId: UUID): Boolean {
        val homeworks = repository.homeworks.getRangeByCredentialsId(dateFrom,dateTo,userId).first()

        if (homeworks.isNotEmpty()) {
            val lesson = homeworks[0]

            val duration = Duration.between(lesson.lastSync, LocalDateTime.now())
            return duration.toMinutes() >= 15
        } else
            return true
    }

    private suspend fun syncHomeworksAtSwitch(client: UserClient, student: Student, dateFrom: LocalDate, dateTo: LocalDate, userId: UUID) {
        val now = LocalDateTime.now()

        val homeworks = client.getHomework(student, dateFrom, dateTo)
        repository.homeworks.deleteRangeByCredentialsId(dateFrom, dateTo, userId)
        for (homework in homeworks) {
            repository.homeworks.insert(
                Homeworks(
                    homework = homework,
                    lastSync = now,
                    credentialsId = userId
                )
            )
        }
    }

    private suspend fun syncHomeworks() {
        if (!checkIfHomeworksShouldBeSync(dateFrom.value, dateTo.value, userId!!)) {
            updateHomeworks()
            return
        }

        if (!userClientCredentialsRenewed) {
            client?.renewCredentials()
            userClientCredentialsRenewed = true
        }

        homeworkRefreshing.value = true

        try {
            loadingError.value = false
            syncHomeworksAtSwitch(client!!,student!!,dateFrom.value,dateTo.value, userId!!)

            updateHomeworks()
        } catch (e: Exception) {
            loadingError.value = true
            exception.value = e
        }

        homeworkRefreshing.value = false
    }

    fun onForward() {
        dateFrom.value = nextWeek(dateFrom.value)
        dateTo.value = nextWeek(dateTo.value)

        runOnIOThread {
            syncHomeworks()
        }
    }

    fun onBack() {
        dateFrom.value = previousWeek(dateFrom.value)
        dateTo.value = previousWeek(dateTo.value)

        runOnIOThread {
            syncHomeworks()
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

