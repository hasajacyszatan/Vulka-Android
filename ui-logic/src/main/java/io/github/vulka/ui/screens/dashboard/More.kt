package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.medzik.android.compose.ui.preference.BasicPreference
import io.github.vulka.core.api.Platform
import io.github.vulka.ui.R
import io.github.vulka.ui.VulkaViewModel
import io.github.vulka.ui.screens.auth.Login
import io.github.vulka.ui.screens.dashboard.more.About
import kotlinx.serialization.Serializable

@Serializable
object More

@Composable
fun MoreScreen(
    navController: NavController
) {

    Column {
        BasicPreference(
            leading = { Icon(Icons.Default.Mail, contentDescription = null) },
            title = stringResource(R.string.More_Messages),
            onClick = {  }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
            title = stringResource(R.string.More_Exams),
            onClick = {  }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.Book, contentDescription = null) },
            title = stringResource(R.string.More_Homework),
            onClick = {  }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
            title = stringResource(R.string.More_NotesAndAchievements),
            onClick = {  }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.Settings, contentDescription = null) },
            title = stringResource(R.string.More_Settings),
            onClick = {  }
        )

        BasicPreference(
            leading = { Icon(Icons.Default.Info, contentDescription = null) },
            title = stringResource(R.string.More_About),
            onClick = { navController.navigate(About) }
        )
    }
}