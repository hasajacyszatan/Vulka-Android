package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.DrawablePainter
import dev.medzik.android.compose.ui.preference.BasicPreference
import io.github.vulka.ui.R
import kotlinx.serialization.Serializable

@Serializable
object About

@Composable
fun AboutScreen() {
    val context = LocalContext.current

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

        Column {
            BasicPreference(
                leading = { Icon(Icons.Default.Info, contentDescription = null) },
                title = stringResource(R.string.More_About),
                onClick = { }
            )
        }
    }
}