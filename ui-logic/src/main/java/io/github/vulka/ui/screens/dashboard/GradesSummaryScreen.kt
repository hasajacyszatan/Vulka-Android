package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vulka.core.api.types.Summary
import io.github.vulka.ui.R
import io.github.vulka.ui.common.EmptyViewProgress
import io.github.vulka.ui.common.SegmentedButtonItem
import io.github.vulka.ui.common.SegmentedButtons
import io.github.vulka.ui.utils.toJavaLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryTab(
    pullToRefreshState: PullToRefreshState,
    args: Grades,
    viewModel: GradesSummaryViewModel = hiltViewModel()
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

    val semestersList by viewModel.semestersList.collectAsStateWithLifecycle()
    val semester by viewModel.semester.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = pullToRefreshState.nestedScrollConnection)
    ) {
        if (semestersList.isNotEmpty() && semester != null) {
            val summaryList by viewModel.summaryList.collectAsStateWithLifecycle()

            val endAverage by viewModel.endAverage.collectAsStateWithLifecycle()
            val calculatedAverage by viewModel.calculatedAverage.collectAsStateWithLifecycle()

            val endAverageSubjectsCount by viewModel.endAverageSubjects.collectAsStateWithLifecycle()
            val calculatedAverageSubjectsCount by viewModel.calculatedAverageSubjects.collectAsStateWithLifecycle()

            val allSubjectsCount by viewModel.allSubjectsCount.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                        for (s in semestersList) {
                            SegmentedButtonItem(
                                selected = semester!!.semester.number == s.semester.number,
                                onClick = {
                                    viewModel.setSemester(s)
                                    viewModel.refreshSummary(args)
                                },
                                label = {
                                    Text("${stringResource(R.string.Semester)} ${s.semester.number}")
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //TODO: implement estimated average
//                    AverageCard {
//                        Text(
//                            fontSize = 12.sp,
//                            text = "Szacowana średnia"
//                        )
//                        Text(
//                            color = MaterialTheme.colorScheme.primary,
//                            fontSize = 30.sp,
//                            text = String.format(
//                                Locale.current.toJavaLocale(),
//                                "%.2f",endAverage
//                            ),
//                            modifier = Modifier.padding(6.dp)
//                        )
//                    }

                    AverageCard {
                        Text(
                            fontSize = 12.sp,
                            text = stringResource(R.string.CalculatedAverage)
                        )
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 30.sp,
                            text = String.format(
                                Locale.current.toJavaLocale(),
                                "%.2f",calculatedAverage
                            ),
                            modifier = Modifier.padding(6.dp)
                        )
                        Text(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            text = pluralStringResource(
                                id = R.plurals.SummarySubjectsCount,
                                count = calculatedAverageSubjectsCount,
                                calculatedAverageSubjectsCount,
                                allSubjectsCount
                            )
                        )
                    }

                    AverageCard {
                        Text(
                            fontSize = 12.sp,
                            text = stringResource(R.string.EndAverage)
                        )
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 30.sp,
                            text = String.format(
                                Locale.current.toJavaLocale(),
                                "%.2f",endAverage
                            ),
                            modifier = Modifier.padding(6.dp)
                        )
                        Text(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            text = pluralStringResource(
                                id = R.plurals.SummarySubjectsCount,
                                count = endAverageSubjectsCount,
                                endAverageSubjectsCount,
                                allSubjectsCount
                            )
                        )
                    }
                }


                if (summaryList.isNotEmpty()) {
                    Column {
                        summaryList.forEach {
                            SummaryCard(it)
                        }
                    }
                }
            }
        } else EmptyViewProgress()
    }
}

@Composable
fun SummaryCard(summary: Summary) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = summary.subject)
                Text(text = (summary.average ?: "-").toString())
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.ProposedGrade))
                Text(text = summary.proposedGrade ?: "-")
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.EndGrade))
                Text(text = summary.endGrade ?: "-")
            }
        }

    }
}

@Composable
fun RowScope.AverageCard(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .weight(1f)
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}