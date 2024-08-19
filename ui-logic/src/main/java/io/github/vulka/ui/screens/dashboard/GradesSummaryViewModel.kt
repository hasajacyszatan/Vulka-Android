package io.github.vulka.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.core.api.types.Summary
import io.github.vulka.database.Repository
import io.github.vulka.database.entities.Semesters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GradesSummaryViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val summaryList = MutableStateFlow<List<Summary>>(emptyList())

    val semestersList = MutableStateFlow<List<Semesters>>(emptyList())
    val semester = MutableStateFlow<Semesters?>(null)

    val endAverage = MutableStateFlow(0.0f)
    val calculatedAverage = MutableStateFlow(0.0f)

    val allSubjectsCount = MutableStateFlow(0)
    val calculatedAverageSubjects = MutableStateFlow(0)
    val endAverageSubjects = MutableStateFlow(0)

    fun refresh(args: Grades) {
        viewModelScope.launch(Dispatchers.IO) {
            val collectedSemesters =
                repository.semesters.getByCredentialsId(UUID.fromString(args.userId)).first()
            semester.value = collectedSemesters.find { it.semester.current }
            semestersList.value = collectedSemesters

            if (semester.value != null)
                refreshSummary(args)
        }
    }

    fun refreshSummary(args: Grades) {
        viewModelScope.launch(Dispatchers.IO) {
            val collectedSummary = repository.summary
                .getBySemesterAndCredentialsId(
                    semester.value!!.semester.number,
                    UUID.fromString(args.userId)
                )
                .first()

            summaryList.value = collectedSummary.map { it.summary }.filterNot {
                // filter empty subject
                it.endGrade == null && it.average == null && it.proposedGrade == null
            }.sortedBy { it.subject }

            val (eAverage,eSubjects) = calculateEndAverage(summaryList.value)
            endAverage.value = eAverage
            val (cAverage,cSubjects) = calculateCalculatedAverage(summaryList.value)
            calculatedAverage.value = cAverage

            allSubjectsCount.value = countAllSubjects(summaryList.value)
            calculatedAverageSubjects.value = cSubjects
            endAverageSubjects.value = eSubjects
        }
    }

    fun setSemester(semesters: Semesters) {
        semester.value = semesters
    }

    // TODO: fix subject counters

    private fun countAllSubjects(summary: List<Summary>): Int {
        return summary.filter { it.average != null }.size
    }

    private fun calculateCalculatedAverage(summary: List<Summary>): Pair<Float,Int> {
        val grades = summary.mapNotNull { it.average }
        val sum = grades.sum()
        return Pair(sum / grades.size,grades.size)
    }

    private fun calculateEndAverage(summary: List<Summary>): Pair<Float,Int> {
        val grades = summary.filter { it.average != null || it.endGrade != null }.mapNotNull { it.endGrade?.toIntOrNull() }
        val sum = grades.sum()
        return Pair(sum.toFloat() / grades.size,grades.size)
    }
}