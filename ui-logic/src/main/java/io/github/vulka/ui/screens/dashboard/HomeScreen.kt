package io.github.vulka.ui.screens.dashboard

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Looks6
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import dev.medzik.android.compose.rememberMutable
import dev.medzik.android.compose.ui.IconBox
import dev.medzik.android.compose.ui.dialog.DialogState
import dev.medzik.android.compose.ui.dialog.PickerDialog
import dev.medzik.android.compose.ui.dialog.rememberDialogState
import io.github.vulka.core.api.Platform
import io.github.vulka.database.entities.Credentials
import io.github.vulka.ui.R
import io.github.vulka.ui.common.Avatar
import io.github.vulka.ui.common.ErrorDialog
import io.github.vulka.ui.screens.dashboard.more.AccountManager
import io.github.vulka.ui.utils.getInitials
import kotlinx.serialization.Serializable

@Serializable
class Home(
    val platform: Platform,
    val userId: String,
    val credentials: String,
    val firstSync: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    args: Home,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var scaffoldTitle: String? by rememberMutable { null }
    val pullToRefreshState = rememberPullToRefreshState()
    val dialogState = rememberDialogState()
    val snackBarState by rememberMutable { SnackbarHostState() }
    val errorDialogState = rememberDialogState()
    var exception: Exception? by rememberMutable { null }

    val studentState by viewModel.student.collectAsState()
    val bottomSelected by viewModel.bottomSelected.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(args)

        if (!viewModel.wasRefreshed) {
            pullToRefreshState.startRefresh()
            viewModel.wasRefreshed = true
        }
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.syncUi(context, args, studentState!!, snackBarState, pullToRefreshState) {
                exception = it
                errorDialogState.show()
            }
        }
    }

    @Composable
    fun BoxScope.pullToRefresh() {
        PullToRefreshContainer(
            modifier = Modifier.align(alignment = Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = scaffoldTitle ?: when (bottomSelected) {
                            Start -> stringResource(R.string.Home)
                            Grades -> stringResource(R.string.Grades)
                            Attendance -> stringResource(R.string.Grades)
                            Timetable -> stringResource(R.string.Timetable)
                            More -> stringResource(R.string.More)
                            else -> "Unknown"
                        }
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        if (studentState != null) {
                            Avatar(
                                modifier = Modifier.padding(),
                                text = studentState!!.getInitials(),
                                onClick = {
                                    dialogState.show()
                                }
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        bottomBar = {
            @Composable
            fun NavigationBarItemLabel(@StringRes resId: Int) {
                Text(
                    text = stringResource(resId),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            NavigationBar {
                NavigationBarItem(
                    icon = {
                        IconBox(Icons.Default.Dashboard)
                    },
                    label = { NavigationBarItemLabel(R.string.Home) },
                    selected = bottomSelected == Start ,
                    onClick = {
                        viewModel.setBottomSelected(Start)
                    },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    icon = {
                        IconBox(Icons.Default.Looks6)
                    },
                    label = { NavigationBarItemLabel(R.string.Grades) },
                    selected = bottomSelected == Grades,
                    onClick = {
                        viewModel.setBottomSelected(Grades)
                    },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    icon = {
                        IconBox(Icons.AutoMirrored.Filled.EventNote)
                    },
                    label = { NavigationBarItemLabel(R.string.Attendance) },
                    selected = bottomSelected == Attendance,
                    onClick = {
                        viewModel.setBottomSelected(Attendance)
                    },
                    alwaysShowLabel = false,
                )
                NavigationBarItem(
                    icon = {
                        IconBox(Icons.Default.Backpack)
                    },
                    label = { NavigationBarItemLabel(R.string.Timetable) },
                    selected = bottomSelected == Timetable,
                    onClick = {
                        viewModel.setBottomSelected(Timetable)
                    },
                    alwaysShowLabel = false,
                )
                NavigationBarItem(
                    icon = {
                        IconBox(Icons.AutoMirrored.Filled.Notes)
                    },
                    label = { NavigationBarItemLabel(R.string.More) },
                    selected = bottomSelected == More,
                    onClick = {
                        viewModel.setBottomSelected(More)
                    },
                    alwaysShowLabel = false
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            modifier = Modifier.padding(innerPadding),
            targetState = bottomSelected,
            label = "bottom navigation"
        ) { target ->
            if (target != More) {
                scaffoldTitle = null
            }

            when (target) {
                Start -> {
                    StartScreen(
                        args = Start(
                            platform = args.platform,
                            userId = args.userId
                        ),
                        pullToRefreshState = pullToRefreshState,
                        pullRefresh = {
                            pullToRefresh()
                        },
                    )
                }
                Grades -> {
                    GradesScreen(
                        args = Grades(
                            platform = args.platform,
                            userId = args.userId
                        ),
                        pullToRefreshState = pullToRefreshState,
                        pullRefresh = {
                            pullToRefresh()
                        },
                    )
                }
                Attendance -> {
                    AttendanceScreen(
                        args = Attendance(
                            platform = args.platform,
                            userId = args.userId,
                            credentials = args.credentials
                        )
                    )
                }
                Timetable -> {
                    TimetableScreen(
                        args = Timetable(
                            platform = args.platform,
                            userId = args.userId,
                            credentials = args.credentials
                        ),
                        pullToRefreshState = pullToRefreshState,
                        pullRefresh = {
                            pullToRefresh()
                        },
                    )
                }
                More -> {
                    MoreScreen(
                        args = More(
                            platform = args.platform,
                            userId = args.userId,
                            credentials = args.credentials
                        ),
                        navHostController = navController,
                        changeScaffoldTitle = {
                            scaffoldTitle = it
                        }
                    )
                }
            }
        }
    }

    if (studentState != null) {
        SelectAccount(
            state = dialogState,
            credentials = viewModel.dbCredentials.value!!,
            navController = navController,
            args = args
        )
    }

    ErrorDialog(
        dialogState = errorDialogState,
        error = exception
    )
}

@Composable
fun SelectAccount(
    args: Home,
    state: DialogState,
    credentials: Credentials,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val students by viewModel.getAllStudents().collectAsStateWithLifecycle(emptyList())

    PickerDialog(
        state = state,
        title = stringResource(R.string.SelectAccount),
        items = students,
        onSelected = {
            navController.navigate(
                Home(
                    userId = it.id.toString(),
                    platform = it.platform,
                    credentials = it.data,
                    firstSync = false
                )
            ) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        },
        trailing = {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    state.hide()
                    navController.navigate(AccountManager(
                        userId = args.userId,
                        platform = args.platform
                    ))
                }
            ) {
                Text(text = stringResource(R.string.ManageAccounts))
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(10.dp),
        ) {
            Avatar(
                modifier = if (it.id == credentials.id)
                    Modifier.border(1.dp,MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                else
                    Modifier,
                text = it.student.getInitials()
            )
            Column(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                if (it.student.isParent) {
                    Text(it.student.fullName)
                    Text(
                        text = "${it.student.parent!!.fullName} - ${stringResource(R.string.Parent)}",
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
    }
}