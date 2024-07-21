package io.github.vulka.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.LuckyNumber
import io.github.vulka.database.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val student = MutableStateFlow<Student?>(null)
    val luckyNumber = MutableStateFlow<LuckyNumber?>(null)
    val grades = MutableStateFlow<List<io.github.vulka.database.Grades>>(emptyList())
    val timetable = MutableStateFlow<List<io.github.vulka.database.Timetable>>(emptyList())

    fun init(args: Start) {
        viewModelScope.launch {
            repository.luckyNumber.get(UUID.fromString(args.userId)).collect {
                luckyNumber.value = it
            }
        }
        viewModelScope.launch {
            repository.credentials.getById(UUID.fromString(args.userId)).collect {
                student.value = it!!.student
            }
        }
        viewModelScope.launch {
            repository.grades.getFromLastWeek(UUID.fromString(args.userId), LocalDate.now().minusWeeks(1)).collect {
                grades.value = it
            }
        }
        viewModelScope.launch {
            repository.timetable.getByDateAndCredentialsId(UUID.fromString(args.userId), LocalDate.now()).collect {
                timetable.value = it
            }
        }
    }
}