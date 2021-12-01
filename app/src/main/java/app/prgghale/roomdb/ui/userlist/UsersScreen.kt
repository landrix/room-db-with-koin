package app.prgghale.roomdb.ui.userlist

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.prgghale.roomdb.composables.AppScaffold
import app.prgghale.roomdb.composables.ShowAlertDialog
import app.prgghale.roomdb.data.domain.UserProfession
import app.prgghale.roomdb.data.table.UserTable
import app.prgghale.roomdb.extesion.toastS
import app.prgghale.roomdb.iconFilled
import app.prgghale.roomdb.iconOutlined
import app.prgghale.roomdb.ui.home.UserViewModel
import app.prgghale.roomdb.utils.UiStates
import org.koin.androidx.compose.getViewModel

@Preview(showSystemUi = true)
@Composable
private fun UsersPreview() {
    UsersContent(users = emptyList()) {}
}

@Composable
fun UsersScreen(usersViewModel: UserViewModel = getViewModel()) {
    val usersState = usersViewModel.userProfession.collectAsState()
    val deleteState = usersViewModel.delete.observeAsState()
    val context = LocalContext.current
    val getData = remember { mutableStateOf(true) }
    var showAlertState by remember {
        mutableStateOf<UserTable?>(null)
    }

    if (getData.value) {
        getData.value = false
        usersViewModel.getUserProfession()
    }
    when (val state = usersState.value) {
        is UiStates.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiStates.Success -> {
            UsersContent(
                users = state.data
            ) {
                showAlertState = it
            }
        }
        is UiStates.Error -> {
            context.toastS(state.message)
        }
    }

    if (showAlertState != null)
        ShowAlertDialog(
            onDismiss = { showAlertState = null },
            onDelete = {
                usersViewModel.deleteUser(user = showAlertState!!)
            }
        )

    when (val state = deleteState.value) {
        is UiStates.Loading -> {
            // DO Nothing
        }
        is UiStates.Success -> {
            showAlertState = null
            usersViewModel.refreshUser()
        }
        is UiStates.Error -> {
            context.toastS(state.message)
        }
    }
}

@Composable
private fun UsersContent(
    users: List<UserProfession>?,
    onDelete: (user: UserTable) -> Unit
) {
    AppScaffold(title = "Registered Users") {
        LazyColumn(content = {
            itemsIndexed(users.orEmpty()) { _, user ->
                UserItem(user = user, onDelete = onDelete)
            }
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UserItem(user: UserProfession, onDelete: (user: UserTable) -> Unit) {
    Column {
        ListItem(
            text = {
                Text(
                    text = user.profession.professionName.orEmpty(),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                )
            },
            secondaryText = {
                Text(
                    text = user.user.address.orEmpty(),
                    style = TextStyle(Color.LightGray)
                )
            },
            overlineText = {
                Text(text = user.user.displayName())
            },
            trailing = {
                TrailingContent(onDelete = { onDelete(user.user) })
            }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TrailingContent(
    onDelete: () -> Unit
) {
    var visibleContent by remember { mutableStateOf(false) }

    Row {
        AnimatedVisibility(
            visible = visibleContent,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Row {
                IconButton(onClick = { }) {
                    Icon(iconOutlined.FavoriteBorder, contentDescription = "Favorite Icon")
                }

                IconButton(onClick = onDelete) {
                    Icon(iconOutlined.Delete, contentDescription = "Delete Icon")
                }
            }
        }

        IconButton(onClick = { visibleContent = !visibleContent }) {
            Icon(
                if (visibleContent) iconFilled.ArrowForwardIos else iconFilled.ArrowBackIos,
                contentDescription = null
            )
        }
    }
}