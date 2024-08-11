package io.github.vulka.ui.screens.dashboard

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
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val currentDate = MutableStateFlow<LocalDate>(LocalDate.now())
    private var client by mutableStateOf<UserClient?>(null)
    private var userClientCredentialsRenewed by mutableStateOf(false)
    private var student by mutableStateOf<Student?>(null)

    var timetableRefreshing = MutableStateFlow(false)

    val exception = MutableStateFlow<Exception?>(null)
    var loadingError = MutableStateFlow(false)

    var userId by mutableStateOf<UUID?>(null)

    val lessons = MutableStateFlow<List<io.github.vulka.database.entities.Timetable>>(emptyList())

    suspend fun init(args: Timetable) = viewModelScope.launch(Dispatchers.IO) {
        client = getUserClientFromCredentials(args.platform, args.credentials)
        student = getStudentFromCredentials(UUID.fromString(args.userId))

        userId = UUID.fromString(args.userId)

        updateLessons()
    }

    fun onBackClick() {
        currentDate.value = getPreviousWeekday(currentDate.value)
        runOnIOThread {
            syncTimetable()
        }
    }

    fun onForwardClick() {
        currentDate.value = getNextWeekday(currentDate.value)
        runOnIOThread {
            syncTimetable()
        }
    }

    fun onDateSet(date: LocalDate) {
        currentDate.value = date
        runOnIOThread {
            syncTimetable()
        }
    }

    private suspend fun syncTimetable() {
        if (!checkIfTimetableShouldBeSync(currentDate.value, userId!!)) {
            updateLessons()
            return
        }

        if (!userClientCredentialsRenewed) {
            client?.renewCredentials()
            userClientCredentialsRenewed = true
        }

        timetableRefreshing.value = true

        try {
            loadingError.value = false
            syncTimetableAtSwitch(client!!,student!!,currentDate.value, userId!!)

            updateLessons()
        } catch (e: Exception) {
            loadingError.value = true
            exception.value = e
        }

        timetableRefreshing.value = false
    }

    suspend fun updateLessons() {
        if (userId != null) {
            val lessonsState = repository.timetable.getByDateAndCredentialsId(userId!!, currentDate.value).first()
            lessons.value = lessonsState.sortedBy { it.lesson.position }
        }
    }

    private suspend fun checkIfTimetableShouldBeSync(selectedDateTime: LocalDate, userId: UUID): Boolean {
        val lessons = repository.timetable.getByDateAndCredentialsId(userId,selectedDateTime).first()

        if (lessons.isNotEmpty()) {
            val lesson = lessons[0]

            val duration = Duration.between(lesson.lastSync, LocalDateTime.now())
            return duration.toMinutes() >= 15
        } else
            return true
    }

    private suspend fun syncTimetableAtSwitch(client: UserClient, student: Student, selectedDate: LocalDate, userId: UUID) {
        val now = LocalDateTime.now()

        val lessons = client.getLessons(student, selectedDate)
        repository.timetable.deleteRangeByCredentialsId(selectedDate, selectedDate, userId)
        for (lesson in lessons) {
            repository.timetable.insert(
                io.github.vulka.database.entities.Timetable(
                    lesson = lesson,
                    lastSync = now,
                    credentialsId = userId
                )
            )
        }
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


    private fun getNextWeekday(date: LocalDate): LocalDate {
        return date.plusDays(1)
    }

    private fun getPreviousWeekday(date: LocalDate): LocalDate {
        return date.minusDays(1)
    }
}