package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.medzik.android.compose.theme.combineAlpha
import dev.medzik.android.compose.ui.dialog.rememberDialogState
import io.github.vulka.core.api.Platform
import io.github.vulka.database.Homeworks
import io.github.vulka.ui.R
import io.github.vulka.ui.common.DatePagerRange
import io.github.vulka.ui.common.EmptyView
import io.github.vulka.ui.common.EmptyViewProgress
import io.github.vulka.ui.common.ErrorDialog
import io.github.vulka.ui.utils.toJavaLocale
import kotlinx.serialization.Serializable
import java.time.format.TextStyle

@Serializable
class Homework(
    val platform: Platform,
    val userId: String,
    val credentials: String
)

@Composable
fun HomeworkScreen(
    args: Homework,
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.init(args)
    }

    val dateFrom by viewModel.dateFrom.collectAsStateWithLifecycle()
    val dateTo by viewModel.dateTo.collectAsStateWithLifecycle()

    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val loadingError by viewModel.loadingError.collectAsStateWithLifecycle()

    val homeworkRefreshing by viewModel.homeworkRefreshing.collectAsStateWithLifecycle()
    val errorDialogState = rememberDialogState()

    val homeworks by viewModel.homeworks.collectAsStateWithLifecycle()

    DatePagerRange(
        dateFrom = dateFrom,
        dateTo = dateTo,
        onClickForward = { viewModel.onForward() },
        onClickBack = { viewModel.onBack() }
    ) {
        if (loadingError) {
            EmptyView(
                icon = Icons.Default.Book,
                title = "${stringResource(R.string.Error)}: ${exception?.message}",
                fontSize = 15.sp,
                textPadding = 20.dp,
                textAlign = TextAlign.Center
            ) {
                OutlinedButton(
                    onClick = {
                        errorDialogState.show()
                    }
                ) {
                    Text(text = stringResource(R.string.Details))
                }
            }
        } else if (!homeworkRefreshing) {
            if (homeworks.isNotEmpty())
                HomeworkCards(homeworks)
            else
                EmptyView(icon = Icons.Default.Book, title = stringResource(R.string.More_Homework_Empty))
        } else EmptyViewProgress()
    }

    ErrorDialog(
        dialogState = errorDialogState,
        error = exception
    )
}

@Composable
fun HomeworkCards(
    homeworks: List<Homeworks>
) {
    val groupedHomeworks = homeworks.groupBy { it.homework.deadline.dayOfWeek }

    LazyColumn {
        groupedHomeworks.forEach { (dayOfWeek, homeworksForDay) ->
            item {
                WeekDayHeading(
                    weekday = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.current.toJavaLocale())
                        .capitalize(Locale.current)
                )
            }
            homeworksForDay.forEach { homeworkItem ->
                item {
                    HomeworkCard(homeworkItem.homework)
                }
            }
        }
    }
}

@Composable
fun WeekDayHeading(
    weekday: String
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .padding(3.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer.combineAlpha(0.5f)
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            text = weekday
        )
    }
}

@Composable
fun HomeworkCard(
    homework: io.github.vulka.core.api.types.Homework
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = homework.subject,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = homework.creator,
                    fontSize = 12.sp
                )
            }

            Text(text = homework.content)
        }
    }
}