package io.github.vulka.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.ui.IconBox
import io.github.vulka.ui.R
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePager(
    modifier: Modifier = Modifier,
    date: LocalDate,
    onClickBack: DatePagerScope.() -> Unit = {},
    onClickForward: DatePagerScope.() -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    content: @Composable (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var datePickerDialogShow by rememberMutable { false }

    Column(
        modifier = modifier
    ) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            targetState = date,
            label = "date pager"
        ) { date ->
            content(date)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onClickBack(DatePagerScope()) }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                    )
                }

                Text(
                    fontWeight = FontWeight.Bold,
                    text = formatDate(date),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { datePickerDialogShow = true }
                        .padding(5.dp)
                )

                IconButton(
                    onClick = { onClickForward(DatePagerScope()) }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    )
                }
            }
        }
    }

    if (datePickerDialogShow) {
        DatePickerDialog(
            onDismissRequest = {
                datePickerDialogShow = false
            },
            confirmButton = {
                Button(
                    modifier = Modifier.padding(5.dp),
                    enabled = datePickerState.selectedDateMillis != null,
                    onClick = {
                        onDateSelected(LocalDate.ofEpochDay(
                            Duration.ofMillis(datePickerState.selectedDateMillis!!).toDays()))
                        datePickerDialogShow = false
                    }
                ) {
                    Text(text = stringResource(R.string.Select))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DatePagerRange(
    modifier: Modifier = Modifier,
    dateFrom: LocalDate,
    dateTo: LocalDate,
    onClickBack: DatePagerScope.() -> Unit = {},
    onClickForward: DatePagerScope.() -> Unit = {},
    targetState: Any? = null,
    content: @Composable (Any?) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            targetState = targetState ?: dateFrom,
            label = "date pager"
        ) { target ->
            content(target)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onClickBack(DatePagerScope()) }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                    )
                }

                Text(
                    fontWeight = FontWeight.Bold,
                    text = formatDateWeek(dateFrom,dateTo)
                )

                IconButton(
                    onClick = { onClickForward(DatePagerScope()) }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    )
                }
            }
        }
    }
}

class DatePagerScope {
    private fun getNextWeekday(date: LocalDate): LocalDate {
        return date.plusDays(1)
    }

    private fun getPreviousWeekday(date: LocalDate): LocalDate {
        return date.minusDays(1)
    }

    fun nextWeek(date: LocalDate): LocalDate {
        return date.plusWeeks(1)
    }

    fun previousWeek(date: LocalDate): LocalDate {
        return date.minusWeeks(1)
    }
}

private fun formatDateWeek(date: LocalDate,date2: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM")
    return "${date.format(formatter)} - ${date2.format(formatter)}"
}

private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
    return date.format(formatter)
}