package io.github.vulka.ui.screens.dashboard.more

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.database.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    var dateFrom = MutableStateFlow(LocalDate.now().with(
        TemporalAdjusters.previousOrSame(
        DayOfWeek.MONDAY)))
    var dateTo = MutableStateFlow(LocalDate.now().with(
        TemporalAdjusters.nextOrSame(
        DayOfWeek.SUNDAY)))

    fun onForward() {
        dateFrom.value = nextWeek(dateFrom.value)
        dateTo.value = nextWeek(dateTo.value)
    }

    fun onBack() {
        dateFrom.value = previousWeek(dateFrom.value)
        dateTo.value = previousWeek(dateTo.value)
    }

    private fun nextWeek(date: LocalDate): LocalDate {
        return date.plusWeeks(1)
    }

    private fun previousWeek(date: LocalDate): LocalDate {
        return date.minusWeeks(1)
    }
}

