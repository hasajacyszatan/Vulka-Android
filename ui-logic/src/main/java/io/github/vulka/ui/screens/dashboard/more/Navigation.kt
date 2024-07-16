package io.github.vulka.ui.screens.dashboard.more

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.vulka.ui.R
import io.github.vulka.ui.TopBarWithBack
import io.github.vulka.ui.common.DefaultScaffold

fun NavGraphBuilder.moreNavigation(navController: NavController) {
    composable<About> {
        DefaultScaffold(
            topBar = {
                TopBarWithBack(
                    title = R.string.More_About,
                    navController
                )
            }
        ) {
            AboutScreen()
        }
    }
}