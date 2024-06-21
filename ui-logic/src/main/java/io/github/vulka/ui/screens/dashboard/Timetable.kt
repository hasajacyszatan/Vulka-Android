package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.medzik.android.compose.ui.IconBox
import io.github.vulka.ui.VulkaViewModel
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("pl"))
        return date.format(formatter)
    }

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

    Column {
        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f)
        ) {
            val lessons = viewModel.timetableRepository.getByDateAndCredentialsId(UUID.fromString(args.userId),currentDate)
                .sortedBy { it.lesson.position }

            if (lessons.isNotEmpty()) {
                lessons.forEach {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(5.dp),

                            ) {
                            Column (
                                modifier = Modifier.width(50.dp).height(70.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    fontSize = 30.sp,
                                    text = "${it.lesson.position}"
                                )
                            }


                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .height(70.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(
                                        modifier = Modifier.width(50.dp)
                                    ) {
                                        Text(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            text = it.lesson.startTime
                                        )
                                        Text(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            text = it.lesson.endTime
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            fontSize = 20.sp,
                                            text = it.lesson.subjectName
                                        )
                                        Text(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            text = it.lesson.teacherName
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("No lessons")
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
