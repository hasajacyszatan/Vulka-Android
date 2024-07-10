package io.github.vulka.ui.screens.dashboard.more

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.DrawablePainter
import io.github.vulka.ui.R
import io.github.vulka.ui.common.CardPreference
import kotlinx.serialization.Serializable

const val DISCORD_INVITE_URL = "https://discord.com/invite/BbPFHYa7FR"
const val GITHUB_URL = "https://github.com/VulkaProject"

@Serializable
object About

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // get app icon
    val icon = context.packageManager.getApplicationIcon(context.packageName)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = DrawablePainter(icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "Vulka",
                fontSize = 25.sp,
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CardPreference(
                leading = { Icon(Icons.Default.Info, contentDescription = null) },
                title = stringResource(R.string.More_About_Version),
                subtitle = context.getAppVersion(),
                onClick = { }
            )

            CardPreference(
                leading = { Icon(painterResource(R.drawable.ic_about_github), contentDescription = null) },
                title = stringResource(R.string.More_About_Github),
                subtitle = stringResource(R.string.More_About_Github_Subtitle),
                onClick = { uriHandler.openUri(GITHUB_URL) }
            )

            CardPreference(
                leading = { Icon(painterResource(R.drawable.ic_about_discord), contentDescription = null) },
                title = stringResource(R.string.More_About_Discord),
                subtitle = stringResource(R.string.More_About_Discord_Subtitle),
                onClick = { uriHandler.openUri(DISCORD_INVITE_URL) }
            )
        }
    }
}

// Can't use BuildConfig in another modules
fun Context.getAppVersion(): String = packageManager.getPackageInfo(packageName, 0).versionName
