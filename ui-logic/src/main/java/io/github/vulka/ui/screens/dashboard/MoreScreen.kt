package io.github.vulka.ui.screens.dashboard

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import dev.medzik.android.compose.ui.preference.BasicPreference
import io.github.vulka.ui.R
import io.github.vulka.ui.screens.dashboard.more.About
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.vulka.business.utils.getFeaturesByPlatform
import io.github.vulka.core.api.Platform
import io.github.vulka.ui.screens.dashboard.more.Exams
import io.github.vulka.ui.screens.dashboard.more.ExamsScreen
import io.github.vulka.ui.screens.dashboard.more.Homework
import io.github.vulka.ui.screens.dashboard.more.HomeworkScreen
import io.github.vulka.ui.screens.dashboard.more.Meetings
import io.github.vulka.ui.screens.dashboard.more.MeetingsScreen
import io.github.vulka.ui.screens.dashboard.more.Messages
import io.github.vulka.ui.screens.dashboard.more.MessagesScreen
import io.github.vulka.ui.screens.dashboard.more.Notes
import io.github.vulka.ui.screens.dashboard.more.NotesScreen
import io.github.vulka.ui.screens.dashboard.more.Settings
import io.github.vulka.ui.screens.dashboard.more.SettingsScreen
import io.github.vulka.ui.utils.navtype.PlatformType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
class More(
    val platform: Platform,
    val userId: String
)

@Composable
fun MoreScreen(
    args: More,
    navHostController: NavController,
    changeScaffoldTitle: @Composable (String?) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = More(
            platform = args.platform,
            userId = args.userId
        ),
        modifier = Modifier.imePadding(),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable<More>(
            typeMap = mapOf(typeOf<Platform>() to PlatformType)
        ) {
            changeScaffoldTitle(null)
            MoreContent(navHostController, navController, args.userId, args.platform)
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
            NotesScreen(
                args = it.toRoute<Notes>()
            )
        }

        composable<Meetings> {
            changeScaffoldTitle(stringResource(R.string.More_Meetings))
            MeetingsScreen(
                args = it.toRoute<Meetings>()
            )
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
    userId: String,
    platform: Platform,
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
            onClick = {
                navController.navigate(
                    Notes(
                        userId = userId
                    )
                )
            }
        )

        if (getFeaturesByPlatform(platform).isMeetingsSupported) {
            BasicPreference(
                leading = { Icon(Icons.Default.Group, contentDescription = null) },
                title = stringResource(R.string.More_Meetings),
                onClick = {
                    navController.navigate(
                        Meetings(
                            userId = userId
                        )
                    )
                }
            )
        }

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