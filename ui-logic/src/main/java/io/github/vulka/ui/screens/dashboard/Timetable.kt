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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.medzik.android.compose.color.combineAlpha
import dev.medzik.android.compose.color.warningContainer
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.ui.IconBox
import dev.medzik.android.compose.ui.bottomsheet.BaseBottomSheet
import dev.medzik.android.compose.ui.bottomsheet.rememberBottomSheetState
import dev.medzik.android.compose.ui.dialog.rememberDialogState
import dev.medzik.android.compose.ui.textfield.AnimatedTextField
import dev.medzik.android.compose.ui.textfield.TextFieldValue
import dev.medzik.android.utils.runOnIOThread
import io.github.vulka.business.sync.checkIfTimetableShouldBeSync
import io.github.vulka.business.sync.getStudentFromCredentials
import io.github.vulka.business.sync.getUserClientFromCredentials
import io.github.vulka.business.sync.syncTimetableAtSwitch
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.LessonChangeType
import io.github.vulka.ui.R
import io.github.vulka.ui.VulkaViewModel
import io.github.vulka.ui.common.ErrorDialog
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
class Timetable(
    val platform: Platform,
    val userId: String,
    val credentials: String
)

@Composable
fun TimetableScreen(
    args: Timetable,
    viewModel: VulkaViewModel = hiltViewModel()
) {

    val context = LocalContext.current

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
    fun getWeekday(date: LocalDate): LocalDate {
        var previousDate = date
        while (previousDate.dayOfWeek == DayOfWeek.SATURDAY || previousDate.dayOfWeek == DayOfWeek.SUNDAY) {
            previousDate = previousDate.plusDays(1)
        }
        return previousDate
    }

    var currentDate by rememberMutable(getWeekday(LocalDate.now()))

    var timetableRefreshing by rememberMutable(false)

    val client by rememberMutable(getUserClientFromCredentials(args.platform, args.credentials))
    var userClientCredentialsRenewed by rememberMutable(false)
    val student by rememberMutable(getStudentFromCredentials(context,UUID.fromString(args.userId)))

    var exception: Exception? by rememberMutable(null)
    var loadingError by rememberMutable(false)
    val errorDialogState = rememberDialogState()

    fun syncTimetable() {
        if (!checkIfTimetableShouldBeSync(context, currentDate,UUID.fromString(args.userId)))
            return

        runOnIOThread {
            if (!userClientCredentialsRenewed) {
                client.renewCredentials()
                userClientCredentialsRenewed = true
            }

            timetableRefreshing = true

            try {
                loadingError = false
                syncTimetableAtSwitch(context,client,student,currentDate,UUID.fromString(args.userId))
            } catch (e: Exception) {
                loadingError = true
                exception = e
            }

            timetableRefreshing = false
        }
    }

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

            if (loadingError) {
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
                        modifier = Modifier.padding(20.dp),
                        text = "${stringResource(R.string.Error)}: ${exception?.message}",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )

                    OutlinedButton(
                        onClick = {
                            errorDialogState.show()
                        }
                    ) {
                        Text(text = stringResource(R.string.Details))
                    }
                }
            } else if (!timetableRefreshing) {
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
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
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
                        syncTimetable()
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
                        syncTimetable()
                    }
                ) {
                    IconBox(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    )
                }
            }
        }
    }

    ErrorDialog(
        dialogState = errorDialogState,
        error = exception
    )
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

        val cardColor = if (isOngoing && lesson.change == null) {
            MaterialTheme.colorScheme.primaryContainer
        } else if (isOngoing && lesson.change!!.type == LessonChangeType.Replacement)
            MaterialTheme.colorScheme.primaryContainer
        else if (lesson.change != null) {
            when (lesson.change!!.type) {
                LessonChangeType.Replacement -> MaterialTheme.colorScheme.warningContainer
                LessonChangeType.Canceled -> MaterialTheme.colorScheme.errorContainer
            }
        } else MaterialTheme.colorScheme.surfaceContainer

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .height(70.dp),
            shape = MaterialTheme.shapes.medium,
            color = cardColor
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
                        color = contentColorFor(cardColor).copy(alpha = 0.7f),
                        text = lesson.startTime
                    )
                    Text(
                        fontSize = 15.sp,
                        color = contentColorFor(cardColor).copy(alpha = 0.7f),
                        text = lesson.endTime
                    )
                }

                val wasLessonChanged = lesson.change != null

                Column(
                    modifier = (if (isOngoing) Modifier.weight(3f) else Modifier)
                        .padding(horizontal = 2.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        fontSize = 15.sp,
                        text = if (wasLessonChanged && !lesson.change!!.newSubjectName.isNullOrEmpty())
                            lesson.change!!.newSubjectName!!
                        else
                            lesson.subjectName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (wasLessonChanged && lesson.change!!.type == LessonChangeType.Canceled)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None,
                    )
                    Text(
                        color = contentColorFor(cardColor).copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        text = if (wasLessonChanged)
                            when (lesson.change!!.type) {
                                LessonChangeType.Canceled -> lesson.change!!.message.orEmpty()
                                LessonChangeType.Replacement ->
                                    (if (!lesson.change!!.classRoom.isNullOrEmpty()) "${lesson.change!!.classRoom.orEmpty()} " else "") +
                                            lesson.change!!.newTeacher?.fullName
                            }
                        else
                            (if (!lesson.classRoom.isNullOrEmpty()) "${lesson.classRoom.orEmpty()} " else "") + lesson.teacherName
                    )
                }

                if (!wasLessonChanged || lesson.change?.type != LessonChangeType.Canceled)
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
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedTextField(
            value = TextFieldValue(
                value = lesson.subjectName,
                editable = false
            ),
            label = stringResource(R.string.Lesson),
            leading = {
                IconBox(Icons.Default.Book)
            }
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = lesson.teacherName,
                editable = false
            ),
            label = stringResource(R.string.Teacher),
            leading = {
                IconBox(Icons.Default.Person)
            }
        )

        if (lesson.classRoom != null) {
            AnimatedTextField(
                value = TextFieldValue(
                    value = lesson.classRoom!!,
                    editable = false
                ),
                label = stringResource(R.string.ClassRoom),
                leading = {
                    IconBox(Icons.Default.Room)
                }
            )
        }

        if (lesson.groupName != null) {
            AnimatedTextField(
                value = TextFieldValue(
                    value = lesson.groupName!!,
                    editable = false
                ),
                label = stringResource(R.string.Group),
                leading = {
                    IconBox(Icons.Default.Groups)
                }
            )
        }

        AnimatedTextField(
            value = TextFieldValue(
                value = "${lesson.startTime} - ${lesson.endTime}",
                editable = false
            ),
            label = stringResource(R.string.LessonHours),
            leading = {
                IconBox(Icons.Default.Schedule)
            }
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = formatDate(lesson.date),
                editable = false
            ),
            label = stringResource(R.string.Date),
            leading = {
                IconBox(Icons.Default.CalendarMonth)
            }
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
