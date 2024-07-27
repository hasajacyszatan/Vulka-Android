package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vulka.ui.common.DatePagerRange
import kotlinx.serialization.Serializable

@Serializable
object Homework

@Composable
fun HomeworkScreen(
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val dateFrom by viewModel.dateFrom.collectAsStateWithLifecycle()
    val dateTo by viewModel.dateTo.collectAsStateWithLifecycle()
    DatePagerRange(
        dateFrom = dateFrom,
        dateTo = dateTo,
        onClickForward = { viewModel.onForward() },
        onClickBack = { viewModel.onBack() }
    ) { _,_ ->

    }
}