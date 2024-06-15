package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Looks6
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.medzik.android.components.rememberMutable
import dev.medzik.android.components.ui.DialogState
import dev.medzik.android.components.ui.PickerDialog
import dev.medzik.android.components.ui.rememberDialogState
import dev.medzik.android.utils.runOnIOThread
import dev.medzik.android.utils.runOnUiThread
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.types.Student
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.ui.R
import io.github.vulka.ui.VulkaViewModel
import io.github.vulka.ui.common.Avatar
import io.github.vulka.ui.common.PickerDialogWithComponents
import io.github.vulka.ui.crypto.decryptCredentials
import io.github.vulka.ui.utils.getInitials
import io.github.vulka.ui.utils.navtype.PlatformType
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.reflect.typeOf

@Serializable
class Home(
    val platform: Platform,
    val userId: String,
    val credentials: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    args: Home,
    navController: NavController,
    viewModel: VulkaViewModel = hiltViewModel()
) {

    val bottomNavController = rememberNavController()
    val credentials = decryptCredentials(args.credentials)

    // TODO: make something better (Not string based)
    var bottomSelected by rememberMutable("start")
    val student = viewModel.credentialRepository.getById(UUID.fromString(args.userId))!!.student

    val dialogState = rememberDialogState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (bottomSelected) {
                            "start" -> stringResource(R.string.Home)
                            "grades" -> stringResource(R.string.Grades)
                            "timetable" -> stringResource(R.string.Timetable)
                            else -> ""
                        }
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Avatar(
                            modifier = Modifier.padding(),
                            text = student.getInitials(),
                            onClick = {
                                dialogState.show()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = { Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null
                    ) },
                    label = { Text(stringResource(R.string.Home)) },
                    selected = bottomSelected == "start" ,
                    onClick = {
                        bottomSelected = "start"
                        bottomNavController.navigate(Start(
                            platform = args.platform,
                            userId = args.userId,
                            credentials
                        ))
                    }
                )
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = { Icon(
                        imageVector = Icons.Default.Looks6,
                        contentDescription = null
                    ) },
                    label = { Text(stringResource(R.string.Grades)) },
                    selected = bottomSelected == "grades",
                    onClick = {
                        bottomSelected = "grades"
                        bottomNavController.navigate(Grades)
                    }
                )
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = { Icon(
                        imageVector = Icons.Default.Backpack,
                        contentDescription = null
                    ) },
                    label = { Text(stringResource(R.string.Timetable)) },
                    selected = bottomSelected == "timetable",
                    onClick = {
                        bottomSelected = "timetable"
                        bottomNavController.navigate(Timetable)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            bottomNavController,
            startDestination = Start(
                platform = args.platform,
                userId = args.userId,
                credentials
            ),
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable<Start>(
                typeMap = mapOf(typeOf<Platform>() to PlatformType)
            ) {
                val arg = it.toRoute<Start>()
                StartScreen(arg)
            }
            composable<Grades> {
                GradesScreen()
            }
            composable<Timetable> {
                TimetableScreen()
            }
        }
    }

    SelectAccount(dialogState)
}


@Composable
fun SelectAccount(
    state: DialogState,
    viewModel: VulkaViewModel = hiltViewModel()
) {
    val students = viewModel.credentialRepository.getAll()

    PickerDialogWithComponents(
        state = state,
        title = stringResource(R.string.SelectAccount),
        items = students,
        onSelected = {

        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(10.dp),
            ) {
                Avatar(text = it.student.getInitials())
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    if (it.student.isParent) {
                        Text(it.student.parent!!.name)
                        Text(
                            text = "${it.student.fullName} - ${stringResource(R.string.Parent)}",
                            fontSize = 12.sp
                        )
                    } else {
                        Text(it.student.fullName)
                        Text(
                            text = stringResource(R.string.Student),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        components = {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.AccountManager))
            }
        }
    )
}