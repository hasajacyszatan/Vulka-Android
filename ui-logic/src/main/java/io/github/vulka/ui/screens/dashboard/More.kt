package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import dev.medzik.android.compose.navigation.NavigationAnimations
import dev.medzik.android.compose.ui.preference.BasicPreference
import io.github.vulka.ui.R
import io.github.vulka.ui.screens.dashboard.more.About
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.vulka.ui.screens.dashboard.more.Exams
import io.github.vulka.ui.screens.dashboard.more.ExamsScreen
import io.github.vulka.ui.screens.dashboard.more.Homework
import io.github.vulka.ui.screens.dashboard.more.HomeworkScreen
import io.github.vulka.ui.screens.dashboard.more.Messages
import io.github.vulka.ui.screens.dashboard.more.MessagesScreen
import io.github.vulka.ui.screens.dashboard.more.Notes
import io.github.vulka.ui.screens.dashboard.more.NotesScreen
import io.github.vulka.ui.screens.dashboard.more.Settings
import io.github.vulka.ui.screens.dashboard.more.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object More

@Composable
fun MoreScreen(
    navHostController: NavController,
    changeScaffoldTitle: @Composable (String?) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = More,
        modifier = Modifier.imePadding(),
        enterTransition = {
            NavigationAnimations.enterTransition()
        },
        exitTransition = {
            NavigationAnimations.exitTransition()
        },
        popEnterTransition = {
            NavigationAnimations.popEnterTransition()
        },
        popExitTransition = {
            NavigationAnimations.popExitTransition()
        }
    ) {
        composable<More> {
            changeScaffoldTitle(null)
            MoreContent(navHostController, navController)
        }

        composable<Messages> {
            changeScaffoldTitle(stringResource(R.string.More_Messages))
            MessagesScreen()
        }

        composable<Exams> {
            changeScaffoldTitle(stringResource(R.string.More_Exams))
            ExamsScreen()
        }

        composable<Homework> {
            changeScaffoldTitle(stringResource(R.string.More_Homework))
            HomeworkScreen()
        }

        composable<Notes> {
            changeScaffoldTitle(stringResource(R.string.More_NotesAndAchievements))
            NotesScreen()
        }

        composable<Settings> {
            changeScaffoldTitle(stringResource(R.string.More_Settings))
            SettingsScreen()
        }
    }
}
@Composable
fun MoreContent(
    navHostController: NavController,
    navController: NavController,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        BasicPreference(
            leading = { Icon(Icons.Default.Mail, contentDescription = null) },
            title = stringResource(R.string.More_Messages),
            onClick = { navController.navigate(Messages) }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
            title = stringResource(R.string.More_Exams),
            onClick = { navController.navigate(Exams) }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.Book, contentDescription = null) },
            title = stringResource(R.string.More_Homework),
            onClick = { navController.navigate(Homework) }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
            title = stringResource(R.string.More_NotesAndAchievements),
            onClick = { navController.navigate(Notes) }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.Settings, contentDescription = null) },
            title = stringResource(R.string.More_Settings),
            onClick = { navController.navigate(Settings) }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.Info, contentDescription = null) },
            title = stringResource(R.string.More_About),
            onClick = { navHostController.navigate(About) }
        )
    }
}