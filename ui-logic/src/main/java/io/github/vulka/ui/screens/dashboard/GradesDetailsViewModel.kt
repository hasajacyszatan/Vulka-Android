package io.github.vulka.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Summary
import io.github.vulka.database.Repository
import io.github.vulka.database.entities.Semesters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GradesDetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val semestersList = MutableStateFlow<List<Semesters>>(emptyList())
    val semester = MutableStateFlow<Semesters?>(null)

    val gradesList = MutableStateFlow<List<Grade>>(emptyList())
    val uniqueSubjectNames = MutableStateFlow<List<String>>(emptyList())

    val summaryList = MutableStateFlow<List<Summary>>(emptyList())

    fun refresh(args: Grades) {
        viewModelScope.launch(Dispatchers.IO) {
            val collectedSemesters =
                repository.semesters.getByCredentialsId(UUID.fromString(args.userId)).first()
            semester.value = collectedSemesters.find { it.semester.current }
            semestersList.value = collectedSemesters

            if (semester.value != null)
                refreshGrades(args)
        }
    }

    fun refreshGrades(args: Grades) {
        val userId = UUID.fromString(args.userId)
        viewModelScope.launch(Dispatchers.IO) {
            val gradesReturned = repository.grades
                .getBySemesterAndCredentialsId(
                    semester.value!!.semester.number,
                    userId
                )
                .first()
            gradesList.value = gradesReturned.map { it.grade }
            uniqueSubjectNames.value =
                gradesReturned.distinctBy { it.grade.subject }.map { it.grade.subject }
                    .sortedBy { it }.toList()
        }
        viewModelScope.launch(Dispatchers.IO) {
            val summaryReturned = repository.summary
                .getBySemesterAndCredentialsId(
                    semester.value!!.semester.number,
                    userId
                )
                .first()
            summaryList.value = summaryReturned.map { it.summary }
        }
    }

    fun countGrades(userId: String, semester: Int, subjectName: String) = flow {
        emit(repository.grades.countBySubjectSemesterAndCredentials(
            id = UUID.fromString(userId),
            semester = semester,
            subjectName = subjectName,
        ).first())
    }

    fun setSemester(semesters: Semesters) {
        semester.value = semesters
    }

}