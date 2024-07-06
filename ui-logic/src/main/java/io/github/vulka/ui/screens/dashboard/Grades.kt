package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.ui.ExpandedIfNotEmpty
import dev.medzik.android.compose.ui.IconBox
import dev.medzik.android.utils.runOnUiThread
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.types.Grade
import io.github.vulka.ui.R
import io.github.vulka.ui.VulkaViewModel
import io.github.vulka.ui.common.Avatar
import io.github.vulka.ui.common.AvatarShape
import io.github.vulka.ui.common.SegmentedButtonItem
import io.github.vulka.ui.common.SegmentedButtons
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Grades(
    val platform: Platform,
    val userId: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    args: Grades,
    pullRefresh: @Composable BoxScope.() -> Unit = {},
    pullToRefreshState: PullToRefreshState,
    refreshed: Boolean
) {
    val pagerState = rememberPagerState { 2 }
    val tabs = listOf(
        stringResource(R.string.Grades),
        stringResource(R.string.Summary),
    )

    Box {
        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = @Composable { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        TabRowDefaults.PrimaryIndicator(
                            width = 80.dp,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            runOnUiThread {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        GradesTab(
                            pullToRefreshState = pullToRefreshState,
                            args = args,
                            refreshed = refreshed
                        )
                    }

                    1 -> SummaryTab()
                }
            }
        }

        pullRefresh()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesTab(
    pullToRefreshState: PullToRefreshState,
    args: Grades,
    viewModel: VulkaViewModel = hiltViewModel(),

    refreshed: Boolean
) {
    if (refreshed) {
        val semesters = viewModel.semestersRepository.getByCredentialsId(UUID.fromString(args.userId))

        var semester by rememberMutable(semesters.find { it.semester.current }!!)

        val gradesDb = viewModel.gradesRepository.getBySemesterAndCredentialsId(semester.semester.number, UUID.fromString(args.userId))
        val gradeList: List<Grade> = gradesDb.map { it.grade }
        val uniqueSubjectNames: Set<String> = gradeList.map { it.subject }.sortedBy { it }.toSet()

        Box(
            modifier = Modifier.nestedScroll(connection = pullToRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
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
                                    selected = semester.semester.number == s.semester.number,
                                    onClick = { semester = s },
                                    label = { Text("${stringResource(R.string.Semester)} ${s.semester.number}") }
                                )
                            }
                        }
                    }
                }
                item {
                    uniqueSubjectNames.forEach { subjectName ->
                        SubjectCard(
                            more = {
                                val filterGrades =
                                    gradeList.filter { it.subject == subjectName }

                                filterGrades.forEach { grade ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 32.dp)
                                            .padding(vertical = 2.dp),
                                    ) {
                                        val gradeColor = GradeColor.getColorByGrade(grade)
                                        Avatar(
                                            text = grade.value ?: "",
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
                                            Modifier.fillMaxWidth()
                                                .weight(1f)
                                                .padding(horizontal = 10.dp)
                                        ) {
                                            Text(
                                                text = grade.name,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                fontSize = 12.sp,
                                                text = "${grade.date}  ${stringResource(R.string.Weight)}: ${grade.weight}"
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(subjectName)

                            val gradesAmount = viewModel.gradesRepository.countBySubjectSemesterAndCredentials(
                                id = UUID.fromString(args.userId),
                                semester = semester.semester.number,
                                subjectName = subjectName,
                            )
                            Text(
                                fontSize = 12.sp,
                                text = "$gradesAmount ${pluralStringResource(R.plurals.GradesAmount,gradesAmount)}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    more: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    var showMore by rememberMutable(false)

    Surface(
        onClick = {
            showMore = !showMore
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
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
                    Modifier.fillMaxWidth()
                        .weight(1f)
                ) {
                    content()
                }

                IconBox(
                    if (showMore) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                )
            }

            ExpandedIfNotEmpty(Unit.takeIf { showMore }) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    more()
                }
            }
        }
    }
}

@Composable
fun SummaryTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(stringResource(R.string.Summary))
        }
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
        fun getColorByGrade(grade: Grade): GradeColor? {
            val gradeValue = grade.value ?: return null

            return when {
                gradeValue.startsWith("0") -> ZERO
                gradeValue.startsWith("1") -> ONE
                gradeValue.startsWith("2") -> TWO
                gradeValue.startsWith("3") -> THREE
                gradeValue.startsWith("4") -> FOUR
                gradeValue.startsWith("5") -> FIVE
                gradeValue.startsWith("6") -> SIX
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
    }
}
