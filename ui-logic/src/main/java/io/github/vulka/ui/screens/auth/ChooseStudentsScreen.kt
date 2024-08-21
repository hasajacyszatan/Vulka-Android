package io.github.vulka.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.medzik.android.compose.ui.LoadingButton
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.types.Student
import io.github.vulka.ui.R
import io.github.vulka.ui.common.EmptyViewProgress
import kotlinx.serialization.Serializable

@Serializable
class ChooseStudents(
    val platform: Platform,
    val credentialsData: String
)

@Composable
fun ChooseStudentsScreen(
    args: ChooseStudents,
    navController: NavController,
    viewModel: ChooseStudentsViewModel = hiltViewModel()
) {
    val client = viewModel.getClient(args.credentialsData,args.platform)

    LaunchedEffect(Unit) {
        viewModel.refreshStudents(client)
    }

    if (viewModel.loaded) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.students.forEach { student ->
                    StudentBox(student, viewModel.selectedStudents)
                }
            }

            LoadingButton(
                onClick = {
                    viewModel.addStudents(args.platform,args.credentialsData,navController)
                },
                enabled = viewModel.selectedStudents.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.Done))
            }
        }
    } else EmptyViewProgress()
}

@Composable
private fun StudentBox(student: Student,studentsList: MutableList<Student>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = studentsList.contains(student),
            onCheckedChange = {  if (it) studentsList.add(student) else studentsList.remove(student) }
        )

        Column {
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
    }
}

