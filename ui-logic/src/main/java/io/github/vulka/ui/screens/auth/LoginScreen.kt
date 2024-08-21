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
import dev.medzik.android.compose.theme.warningContainer
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
    var enableLogin by rememberMutable { true }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        item {
            when (args.platform) {
                Platform.VulcanHebe -> VulcanHebe(viewModel) { enableLogin = it }
                Platform.Librus -> Librus(viewModel) { enableLogin = it }
                Platform.VulcanPrometheus -> VulcanPrometheus(viewModel) { enableLogin = it }
            }
        }

        item {
            val dialogState = rememberDialogState()
            var error: Exception? by rememberMutable { null }

            var loading by rememberMutable { false }
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
                enabled = enableLogin,
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

@Composable
fun VulcanHebe(viewModel: LoginViewModel,login: (Boolean) -> Unit) {
    login(true)

    Surface(
        modifier = Modifier.padding(bottom = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.warningContainer.combineAlpha(0.9f)
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "UWAGA! Ten sposób logowania został wycofany przez firmę VULCAN. Nie jest już możliwe zalogowanie się za jego pomocą.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.combineAlpha(0.9f)
        )
    }

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
        label = stringResource(R.string.Field_Token),
        value = TextFieldValue.fromMutableState(viewModel.vulcanHebeToken),
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
        label = stringResource(R.string.Field_Symbol),
        value = TextFieldValue.fromMutableState(viewModel.vulcanHebeSymbol),
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
        label = stringResource(R.string.Field_Pin),
        value = TextFieldValue.fromMutableState(viewModel.vulcanHebePin),
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

@Composable
fun Librus(viewModel: LoginViewModel,login: (Boolean) -> Unit) {
    login(true)
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

@Composable
fun VulcanPrometheus(viewModel: LoginViewModel, login: (Boolean) -> Unit) {
    login(true)

    Surface(
        modifier = Modifier.padding(bottom = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.warningContainer.combineAlpha(0.9f)
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "UWAGA! Ten sposób logowania jest eksperymentalny. Może zawierać błędy.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.combineAlpha(0.9f)
        )
    }

    Surface(
        modifier = Modifier.padding(bottom = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.tertiary.combineAlpha(0.9f)
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "Aby zalogować się do aplikacji, użyj tych samych danych, których używasz do logowania się na stronie internetowej eduvulcan.pl",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiary.combineAlpha(0.9f)
        )
    }

    AnimatedTextField(
        modifier = Modifier.padding(vertical = 5.dp),
        label = stringResource(R.string.Field_Login),
        value = TextFieldValue.fromMutableState(viewModel.eduVulcanLogin),
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
        value = TextFieldValue.fromMutableState(viewModel.eduVulcanPassword)
    )

    Surface(
        modifier = Modifier.padding(vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.tertiary.combineAlpha(0.9f)
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "Jeśli wyświetla ci się błąd captcha, zaloguj się na komputerze na eduvulcan.pl, następnie wejdź w https://eduvulcan.pl/api/api, kliknij pokaż kod źródłowy w przeglądarce, znajdź pole \"accessToken\", i wklej do aplikacji, pracujemy nad pełnym wsparciem captchy",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiary.combineAlpha(0.9f)
        )
    }

    AnimatedTextField(
        modifier = Modifier.padding(vertical = 5.dp),
        label = stringResource(R.string.Field_Token),
        value = TextFieldValue.fromMutableState(viewModel.eduVulcanAccessToken),
        clearButton = true,
        singleLine = true,
        leading = {
            Icon(
                imageVector = Icons.Default.DataObject,
                contentDescription = null
            )
        }
    )
}