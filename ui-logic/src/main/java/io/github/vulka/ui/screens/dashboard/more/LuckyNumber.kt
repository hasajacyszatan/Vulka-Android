package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.medzik.android.compose.ui.IconBox
import dev.medzik.android.compose.ui.bottomsheet.BaseBottomSheet
import dev.medzik.android.compose.ui.bottomsheet.BottomSheetState
import io.github.vulka.ui.R
import kotlinx.coroutines.launch

@Composable
fun LuckyNumberBottomSheet(
    bottomSheetState: BottomSheetState,
    luckyNumber: Int
) {
    val scope = rememberCoroutineScope()

    BaseBottomSheet(
        state = bottomSheetState,
        onDismiss = {
            scope.launch { bottomSheetState.hide() }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconBox(
                imageVector = Icons.Default.Star,
                modifier = Modifier.size(100.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = if (luckyNumber != 0) {
                    stringResource(R.string.LuckyNumberText) + " " + luckyNumber.toString()
                } else {
                    stringResource(R.string.LuckyNumberNone)
                },
                fontSize = 20.sp,
            )
        }
    }
}
