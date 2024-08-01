package io.github.vulka.ui.screens.dashboard.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.types.Student
import io.github.vulka.ui.R
import io.github.vulka.ui.common.Avatar
import io.github.vulka.ui.screens.ChoosePlatform
import io.github.vulka.ui.utils.getInitials
import kotlinx.serialization.Serializable

@Serializable
class AccountManager(
    val platform: Platform,
    val userId: String,
)

@Composable
fun AccountManagerScreen(
    args: AccountManager,
    navController: NavController,
    viewModel: AccountManagerViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        item {
            viewModel.credentials.forEach { credentials ->
                StudentCard(
                    student = credentials.student,
                    options = {
                        IconButton(
                            onClick = { viewModel.onDeleteClick(args, credentials, navController) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                )
            }
        }

        item {
            Button(
                onClick = { navController.navigate(ChoosePlatform) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.AddAccount))
            }
        }
    }

    BackHandler {
        viewModel.checkAndBack(navController)
    }
}

@Composable
fun StudentCard(
    student: Student,
    options: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(10.dp),
    ) {
        Avatar(
            text = student.getInitials()
        )

        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (student.isParent) {
                Text(student.fullName)
                Text(
                    text = "${student.parent!!.fullName} - ${stringResource(R.string.Parent)}",
                    fontSize = 12.sp
                )
            } else {
                Text(student.fullName)
                Text(
                    text = stringResource(R.string.Student),
                    fontSize = 12.sp
                )
            }
        }
        options()
    }
}