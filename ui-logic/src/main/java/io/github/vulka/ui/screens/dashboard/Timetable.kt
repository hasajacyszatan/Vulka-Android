package io.github.vulka.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.medzik.android.compose.color.combineAlpha
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.ui.IconBox
import dev.medzik.android.compose.ui.bottomsheet.BaseBottomSheet
import dev.medzik.android.compose.ui.bottomsheet.rememberBottomSheetState
import dev.medzik.android.compose.ui.textfield.AnimatedTextField
import dev.medzik.android.compose.ui.textfield.TextFieldValue
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.ui.R
import io.github.vulka.ui.VulkaViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
class Timetable(
    val userId: String
)

@Composable
fun TimetableScreen(
    args: Timetable,
    viewModel: VulkaViewModel = hiltViewModel()
) {
    fun getNextWeekday(date: LocalDate): LocalDate {
        var nextDate = date.plusDays(1)
        while (nextDate.dayOfWeek == DayOfWeek.SATURDAY || nextDate.dayOfWeek == DayOfWeek.SUNDAY) {
            nextDate = nextDate.plusDays(1)
        }
        return nextDate
    }

    fun getPreviousWeekday(date: LocalDate): LocalDate {
        var previousDate = date.minusDays(1)
        while (previousDate.dayOfWeek == DayOfWeek.SATURDAY || previousDate.dayOfWeek == DayOfWeek.SUNDAY) {
            previousDate = previousDate.minusDays(1)
        }
        return previousDate
    }
    var currentDate by rememberMutable(getNextWeekday(LocalDate.now()))

    Column {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            targetState = currentDate,
            label = "timetable date"
        ) { date ->
            val lessons = viewModel.timetableRepository.getByDateAndCredentialsId(
                UUID.fromString(args.userId),
                date
            ).sortedBy { it.lesson.position }

            if (lessons.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconBox(
                        imageVector = Icons.Default.Backpack,
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = stringResource(R.string.NoLessons),
                        fontSize = 20.sp,
                    )
                }
            } else {
                LessonsCards(lessons)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        currentDate = getPreviousWeekday(currentDate)
                    }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                    )
                }

                Text(
                    fontWeight = FontWeight.Bold,
                    text = formatDate(currentDate)
                )

                IconButton(
                    onClick = {
                        currentDate = getNextWeekday(currentDate)
                    }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonsCards(
    lessons: List<io.github.vulka.database.Timetable>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        if (lessons.isNotEmpty()) {
            lessons.forEach {
                val lesson = it.lesson

                val isOngoing = checkIfOngoing(
                    startTime = lesson.startTime,
                    endTime = lesson.endTime,
                    lessonDate = lesson.date
                )

                item {
                    LessonCard(
                        lesson = lesson,
                        isOngoing = isOngoing,
                        timeCard = {
                            LessonTimeCard(
                                lesson = lesson,
                                isOngoing = isOngoing
                            )
                        }
                    )
                }
            }
        } else {
            item {
                Text(stringResource(R.string.NoLessons))
            }
        }
    }
}

@Composable
private fun RowScope.LessonTimeCard(
    lesson: Lesson,
    isOngoing: Boolean
) {
    val minutesLeft = if (isOngoing) {
        calculateMinutesLeft(lesson.endTime, LocalTime.now())
    } else 0L

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
            .height(70.dp)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top
    ) {
        if (isOngoing) {
            Surface(
                modifier = Modifier.height(25.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimary.combineAlpha(0.7f),
                    fontSize = 10.sp,
                    text = pluralStringResource(
                        R.plurals.MinutesLeft,
                        minutesLeft.toInt(),
                        minutesLeft
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonCard(
    lesson: Lesson,
    isOngoing: Boolean,
    timeCard: @Composable RowScope.() -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
    ) {
        Column(
            modifier = Modifier
                .width(35.dp)
                .height(70.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                fontSize = 30.sp,
                text = "${lesson.position}"
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .height(70.dp),
            shape = MaterialTheme.shapes.medium,
            color = if (isOngoing)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { bottomSheetState.show() }
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.width(50.dp)
                ) {
                    Text(
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        text = lesson.startTime
                    )
                    Text(
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        text = lesson.endTime
                    )
                }


                Column(
                    modifier = (if (isOngoing) Modifier.weight(3f) else Modifier)
                        .padding(horizontal = 2.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        fontSize = 15.sp,
                        text = lesson.subjectName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        text = (if (!lesson.room.isNullOrEmpty()) "${lesson.room.orEmpty()} " else "") + lesson.teacherName
                    )
                }

                timeCard()
            }
        }
    }

    BaseBottomSheet(
        state = bottomSheetState,
        onDismiss = {
            scope.launch {
                bottomSheetState.hide()
            }
        }
    ) {
        LessonDetails(lesson)
    }
}

@Composable
private fun LessonDetails(lesson: Lesson) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedTextField(
            value = TextFieldValue(
                value = lesson.subjectName,
                editable = false
            ),
            label = "Lekcja"
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = lesson.teacherName,
                editable = false
            ),
            label = "Nauczyciel"
        )

        if (lesson.room != null) {
            AnimatedTextField(
                value = TextFieldValue(
                    value = lesson.room!!,
                    editable = false
                ),
                label = "Sala"
            )
        }

        AnimatedTextField(
            value = TextFieldValue(
                value = "${lesson.startTime} - ${lesson.endTime}",
                editable = false
            ),
            label = "Godziny"
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = formatDate(lesson.date),
                editable = false
            ),
            label = "Data"
        )
    }
}

private fun checkIfOngoing(startTime: String, endTime: String, lessonDate: LocalDate): Boolean {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    val start = LocalTime.parse(startTime)
    val end = LocalTime.parse(endTime)
    return currentTime.isAfter(start) && currentTime.isBefore(end) && currentDate == lessonDate
}

private fun calculateMinutesLeft(endTime: String, currentTime: LocalTime): Long {
    val end = LocalTime.parse(endTime)
    return java.time.Duration.between(currentTime, end).toMinutes()
}

private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
    return date.format(formatter)
}
