package io.github.vulka.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.medzik.android.compose.ui.IconBox

@Composable
fun EmptyView(
    icon: ImageVector,
    title: String,
    fontSize: TextUnit = 20.sp,
    textAlign: TextAlign? = null,
    textPadding: Dp = 10.dp,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconBox(
            imageVector = icon,
            modifier = Modifier.size(100.dp)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            modifier = Modifier.padding(textPadding),
            text = title,
            fontSize = fontSize,
            textAlign = textAlign
        )

        content()
    }
}