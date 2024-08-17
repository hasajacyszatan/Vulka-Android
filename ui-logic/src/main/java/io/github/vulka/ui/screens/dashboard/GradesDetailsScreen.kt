package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Looks6
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.ui.ExpandedIfNotEmpty
import dev.medzik.android.compose.ui.IconBox
import dev.medzik.android.compose.ui.bottomsheet.BaseBottomSheet
import dev.medzik.android.compose.ui.bottomsheet.rememberBottomSheetState
import dev.medzik.android.compose.ui.textfield.AnimatedTextField
import dev.medzik.android.compose.ui.textfield.TextFieldValue
import io.github.vulka.core.api.types.Grade
import io.github.vulka.ui.R
import io.github.vulka.ui.common.Avatar
import io.github.vulka.ui.common.AvatarShape
import io.github.vulka.ui.common.EmptyView
import io.github.vulka.ui.common.EmptyViewProgress
import io.github.vulka.ui.common.SegmentedButtonItem
import io.github.vulka.ui.common.SegmentedButtons
import io.github.vulka.ui.utils.formatByLocale
import io.github.vulka.ui.utils.toJavaLocale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesTab(
    pullToRefreshState: PullToRefreshState,
    args: Grades,
    viewModel: GradesDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.refresh(args)
    }

    // refresh UI after sync
    if (!pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.refresh(args)
        }
    }

    val semesters by viewModel.semestersList.collectAsStateWithLifecycle()
    val semester by viewModel.semester.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = pullToRefreshState.nestedScrollConnection)
    ) {
        if (semesters.isNotEmpty() && semester != null) {
            val gradeList by viewModel.gradesList.collectAsStateWithLifecycle()
            val uniqueSubjectNames by viewModel.uniqueSubjectNames.collectAsStateWithLifecycle()

            val summaryList by viewModel.summaryList.collectAsStateWithLifecycle()

            if (gradeList.isNotEmpty() && summaryList.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 50.dp)
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(35.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SegmentedButtons {
                            for (s in semesters) {
                                SegmentedButtonItem(
                                    selected = semester!!.semester.number == s.semester.number,
                                    onClick = {
                                        viewModel.setSemester(s)
                                        viewModel.refreshGrades(args)
                                    },
                                    label = {
                                        Text("${stringResource(R.string.Semester)} ${s.semester.number}")
                                    }
                                )
                            }
                        }
                    }
                    uniqueSubjectNames.forEach { subjectName ->
                        val summary = summaryList.firstOrNull { it.subject == subjectName }

                        SubjectCard(
                            endGradeBadge = {
                                if (summary != null) {
                                    if (summary.proposedGrade != null) {
                                        if (summary.endGrade == null) {
                                            Avatar(
                                                modifier = Modifier.padding(end = 5.dp),
                                                text = summary.proposedGrade!!,
                                                shape = AvatarShape.Rounded
                                            )
                                        } else {
                                            val gradeColor = GradeColor.getColorByValue(summary.endGrade)
                                            Avatar(
                                                modifier = Modifier.padding(end = 5.dp),
                                                text = summary.endGrade.orEmpty(),
                                                shape = AvatarShape.Rounded,
                                                cardColors = if (gradeColor != null)
                                                    CardDefaults.cardColors().copy(
                                                        containerColor = gradeColor.containerColor,
                                                        contentColor = gradeColor.contentColor
                                                    )
                                                else CardDefaults.cardColors()
                                            )
                                        }
                                    }
                                }
                            },
                            more = {
                                gradeList
                                    .filter { it.subject == subjectName }
                                    .sortedByDescending { it.date }
                                    .forEach { grade ->
                                        GradeCard(grade = grade)
                                    }
                            }
                        ) {
                            Text(subjectName)

                            val gradesAmount by produceState(initialValue = 0, subjectName, semester) {
                                if (semester != null) {
                                    viewModel.countGrades(
                                        userId = args.userId,
                                        semester = semester!!.semester.number,
                                        subjectName = subjectName
                                    ).collect { value = it }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    fontSize = 12.sp,
                                    text = "$gradesAmount ${pluralStringResource(R.plurals.GradesAmount,gradesAmount)}"
                                )

                                if (summary?.average != null) {
                                    Text(
                                        fontSize = 12.sp,
                                        text = "${stringResource(R.string.Average)}: ${String.format(
                                            Locale.current.toJavaLocale(),
                                            "%.2f",summary.average
                                        )}"
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                EmptyView(
                    icon = Icons.Default.Looks6,
                    title = stringResource(R.string.Grades_Empty)
                )
            }
        } else EmptyViewProgress()
    }
}

@Composable
fun SubjectCard(
    endGradeBadge: @Composable () -> Unit = {},
    more: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    var showMore by rememberMutable { false }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = {
            showMore = !showMore
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(12.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    content()
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    endGradeBadge()
                    
                    IconBox(
                        if (showMore) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                    )
                }
            }

            ExpandedIfNotEmpty(Unit.takeIf { showMore }) {
                Column(
                    modifier = Modifier.padding(6.dp),
                ) {
                    more()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeCard(grade: Grade) {
    val bottomSheetState = rememberBottomSheetState()
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        onClick = {
            bottomSheetState.show()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 32.dp)
                .padding(3.dp)
        ) {
            val gradeColor = GradeColor.getColorByGrade(grade)
            Avatar(
                text = grade.value.orEmpty(),
                shape = AvatarShape.Rounded,
                cardColors = if (gradeColor != null) {
                    CardDefaults.cardColors()
                        .copy(
                            containerColor = gradeColor.containerColor,
                            contentColor = gradeColor.contentColor
                        )
                } else CardDefaults.cardColors()
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = grade.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        fontSize = 12.sp,
                        text = grade.date.formatByLocale(Locale.current)
                    )
                    Text(
                        fontSize = 12.sp,
                        text = "${stringResource(R.string.Weight)}: ${String.format(
                            Locale.current.toJavaLocale(),
                            "%.2f",grade.weight
                        )}"
                    )
                }
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
        GradesDetails(grade)
    }
}

@Composable
private fun GradesDetails(grade: Grade) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (grade.name.isNotEmpty()) {
            AnimatedTextField(
                value = TextFieldValue(
                    value = grade.name,
                    editable = false
                ),
                label = stringResource(R.string.Name),
                leading = {
                    IconBox(Icons.Default.TextFormat)
                }
            )
        }

        AnimatedTextField(
            value = TextFieldValue(
                value = grade.subject,
                editable = false
            ),
            label = stringResource(R.string.Subject),
            leading = {
                IconBox(Icons.Default.Book)
            }
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = grade.value.orEmpty(),
                editable = false
            ),
            label = stringResource(R.string.Grade),
            leading = {
                IconBox(Icons.Default.Looks6)
            }
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = String.format(
                    Locale.current.toJavaLocale(),
                    "%.2f",grade.weight
                ),
                editable = false
            ),
            label = stringResource(R.string.Weight),
            leading = {
                IconBox(Icons.Default.Scale)
            }
        )

        AnimatedTextField(
            value = TextFieldValue(
                value = grade.date.formatByLocale(Locale.current),
                editable = false
            ),
            label = stringResource(R.string.Date),
            leading = {
                IconBox(Icons.Default.CalendarMonth)
            }
        )

    }
}

private enum class GradeColor(
    val containerColor: Color,
    val contentColor: Color
) {
    ZERO(Color(0xFF960000), Color.White),
    ONE(Color(0xFFD32F2F), Color.Black),
    TWO(Color(0xFFFF774D), Color.Black),
    THREE(Color(0xFFFF9800), Color.Black),
    FOUR(Color(0xFFA0C431), Color.Black),
    FIVE(Color(0xFF76B947), Color.Black), // 0xFF76B947
    SIX(Color(0xFF4CAF50), Color.Black);

    companion object {
        fun recognizeGrade(actual: String, expected: String): Boolean {
            val cleanedActual = actual.replace(" ","")
            val gradeRegex = Regex("^[0-6](\\.5|,5)?$")
            if (!gradeRegex.matches(cleanedActual)) {
                return false
            }
            return if (cleanedActual.endsWith(".5") || cleanedActual.endsWith(",5")) {
                cleanedActual.startsWith(expected)
            } else {
                cleanedActual == expected
            }
        }
        fun getColorByValue(gradeValue: String?): GradeColor? {
            if (gradeValue == null) return null

            return when {
                recognizeGrade(gradeValue,"0") -> ZERO
                recognizeGrade(gradeValue,"1") -> ONE
                recognizeGrade(gradeValue,"2") -> TWO
                recognizeGrade(gradeValue,"3") -> THREE
                recognizeGrade(gradeValue,"4") -> FOUR
                recognizeGrade(gradeValue,"5") -> FIVE
                recognizeGrade(gradeValue,"6") -> SIX
                else -> when (gradeValue.lowercase()) {
                    "-" -> ONE
                    "+" -> SIX
                    // nieprzygotowanie
                    "np" -> TWO
                    // brak zadania
                    "bz" -> TWO
                    else -> null
                }
            }
        }
        fun getColorByGrade(grade: Grade): GradeColor? {
            return getColorByValue(grade.value)
        }
    }
}
