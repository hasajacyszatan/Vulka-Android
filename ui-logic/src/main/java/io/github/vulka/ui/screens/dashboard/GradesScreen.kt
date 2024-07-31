package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.medzik.android.utils.runOnUiThread
import io.github.vulka.core.api.Platform
import io.github.vulka.ui.R
import kotlinx.serialization.Serializable

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
                    0 -> GradesTab(
                        pullToRefreshState = pullToRefreshState,
                        args = args,
                    )
                    1 -> SummaryTab(
                        pullToRefreshState = pullToRefreshState,
                        args = args,
                    )
                }
            }
        }

        pullRefresh()
    }
}