package io.github.vulka.ui.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.medzik.android.compose.ui.dialog.DialogState
import io.github.vulka.core.api.ApiException
import io.github.vulka.ui.R
import java.io.PrintWriter
import java.io.StringWriter

@Composable
fun ErrorDialog(
    dialogState: DialogState,
    error: Exception?
) {
    if (error == null)
        return

    val context = LocalContext.current
    val clipboardManager = LocalContext.current.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    val stackTrace = remember {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        error.printStackTrace(pw)
        sw.toString()
    }

    if (dialogState.isVisible) {
        AlertDialog(
            onDismissRequest = {
                dialogState.hide()
            },
            title = { Text(stringResource(R.string.Error)) },
            text = {
                Column {
                    Text(
                        modifier = Modifier.padding(vertical = 20.dp),
                        text = error.message.orEmpty(),
                        fontSize = 15.sp
                    )

                    if (error !is ApiException) {
                        val scrollState = rememberScrollState()
                        val horizontalScrollState = rememberScrollState()
                        Box(
                            modifier = Modifier
                                .height(200.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .verticalScroll(scrollState)
                                    .horizontalScroll(horizontalScrollState)
                            ) {
                                Column(
                                    modifier = Modifier.padding(5.dp),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    Text(
                                        fontSize = 12.sp,
                                        lineHeight = 12.sp,
                                        text = stackTrace
                                    )
                                }
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogState.hide()
                    }
                ) {
                    Text(text = stringResource(R.string.Cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("", stackTrace))
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                            Toast.makeText(context, context.getText(R.string.Copied), Toast.LENGTH_SHORT).show()

                        dialogState.hide()
                    }
                ) {
                    Text(text = stringResource(R.string.Copy))
                }
            }
        )
    }
}