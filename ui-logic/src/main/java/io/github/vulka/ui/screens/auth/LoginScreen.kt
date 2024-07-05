package io.github.vulka.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.theme.combineAlpha
import dev.medzik.android.compose.ui.LoadingButton
import dev.medzik.android.compose.ui.dialog.rememberDialogState
import dev.medzik.android.compose.ui.textfield.AnimatedTextField
import dev.medzik.android.compose.ui.textfield.PasswordAnimatedTextField
import dev.medzik.android.compose.ui.textfield.TextFieldValue
import dev.medzik.android.utils.runOnIOThread
import io.github.vulka.core.api.Platform
import io.github.vulka.ui.R
import io.github.vulka.ui.common.ErrorDialog
import kotlinx.serialization.Serializable

@Serializable
class Login(val platform: Platform)

@Composable
fun LoginScreen(
    args: Login,
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        item {
            when (args.platform) {
                Platform.Vulcan -> {
                    Surface(
                        modifier = Modifier.padding(bottom = 12.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary.combineAlpha(0.9f)
                    ) {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Aby zalogować się do aplikacji, zaloguj się na stronę e-dziennika na komputerze, przejdź do zakładki \"Dostęp mobilny\", następnie kliknij \"Wygeneruj kod dostępu\" i przepisz podane dane poniżej.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary.combineAlpha(0.9f)
                        )
                    }

                    AnimatedTextField(
                        modifier = Modifier.padding(vertical = 5.dp),
                        label = stringResource(R.string.Field_Symbol),
                        value = TextFieldValue.fromMutableState(viewModel.vulcanSymbol),
                        clearButton = true,
                        singleLine = true,
                        leading = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null
                            )
                        }
                    )

                    AnimatedTextField(
                        modifier = Modifier.padding(vertical = 5.dp),
                        label = stringResource(R.string.Field_Token),
                        value = TextFieldValue.fromMutableState(viewModel.vulcanToken),
                        clearButton = true,
                        singleLine = true,
                        leading = {
                            Icon(
                                imageVector = Icons.Default.DataObject,
                                contentDescription = null
                            )
                        }
                    )

                    AnimatedTextField(
                        modifier = Modifier.padding(vertical = 5.dp),
                        label = stringResource(R.string.Field_Pin),
                        value = TextFieldValue.fromMutableState(viewModel.vulcanPin),
                        clearButton = true,
                        singleLine = true,
                        leading = {
                            Icon(
                                imageVector = Icons.Default.Password,
                                contentDescription = null
                            )
                        }
                    )
                }

                Platform.Librus -> {
                    Surface(
                        modifier = Modifier.padding(bottom = 12.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary.combineAlpha(0.9f)
                    ) {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Aby zalogować się do aplikacji, użyj tych samych danych, których używasz do logowania się na stronie internetowej Librus Synergia: https://portal.librus.pl/rodzina/synergia/loguj.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary.combineAlpha(0.9f)
                        )
                    }

                    AnimatedTextField(
                        modifier = Modifier.padding(vertical = 5.dp),
                        label = stringResource(R.string.Field_Login),
                        value = TextFieldValue.fromMutableState(viewModel.librusLogin),
                        clearButton = true,
                        singleLine = true,
                        leading = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        }
                    )

                    PasswordAnimatedTextField(
                        modifier = Modifier.padding(vertical = 5.dp),
                        label = stringResource(R.string.Field_Password),
                        value = TextFieldValue.fromMutableState(viewModel.librusPassword)
                    )
                }
            }
        }

        item {
            val dialogState = rememberDialogState()
            var error: Exception? by rememberMutable(null)

            var loading by rememberMutable(false)
            LoadingButton(
                onClick = {
                    runOnIOThread {
                        loading = true

                        try {
                            viewModel.login(args.platform, navController)
                        } catch (e: Exception) {
                            error = e
                            dialogState.show()
                        }
                        loading = false
                    }
                },
                loading = loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.Login))
            }

            ErrorDialog(
                dialogState = dialogState,
                error = error
            )
        }
    }
}
