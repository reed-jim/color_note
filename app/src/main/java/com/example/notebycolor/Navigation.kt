package com.example.notebycolor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notebycolor.database.TextNote
import com.example.notebycolor.pages.Calendar
import com.example.notebycolor.pages.Note
import com.example.notebycolor.pages.NoteDetail
import com.example.notebycolor.util.CustomDialog
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

sealed class AppColor(val name: String, val color: Color) {
    object Red : AppColor(name = "Red", color = Color.Red)
    object Purple : AppColor(name = "Purple", color = Color(0xFF800080))
    object Green : AppColor(name = "Green", color = Color.Green)
    object Yellow : AppColor(name = "Yellow", color = Color.Yellow)
    object Blue : AppColor(name = "Blue", color = Color.Blue)
    object Cyan : AppColor(name = "Cyan", color = Color.Cyan)
    object Orange : AppColor(name = "Orange", color = Color(0xFFffa500))
    object Gray : AppColor(name = "Gray", color = Color.Gray)
    object Aqua : AppColor(name = "Aqua", color = Color(0xFF00FFFF))
    object Pink : AppColor(name = "Pink", color = Color(0xFFff1493))
}

val appColors = listOf(
    AppColor.Red,
    AppColor.Purple,
    AppColor.Green,
    AppColor.Yellow,
    AppColor.Blue,
    AppColor.Cyan,
    AppColor.Orange,
    AppColor.Gray,
    AppColor.Aqua,
    AppColor.Pink
)

sealed class BottomAppBarButton(val route: String, val icon: ImageVector) {
    object Note : BottomAppBarButton(route = "note", icon = Icons.Outlined.Home)
    object Calendar : BottomAppBarButton(route = "calendar", icon = Icons.Outlined.Notifications)
    object Search : BottomAppBarButton(route = "search", icon = Icons.Outlined.Search)
    object Setting : BottomAppBarButton(route = "setting", icon = Icons.Outlined.Settings)
}

val items = listOf(
    BottomAppBarButton.Note,
    BottomAppBarButton.Calendar,
    BottomAppBarButton.Search,
    BottomAppBarButton.Setting
)

fun navigateToRoute(navController: NavController, route: String) {
    navController.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val config = RealmConfiguration.Builder(schema = setOf(TextNote::class))
        .deleteRealmIfMigrationNeeded()
        .build()

    val realm: Realm = Realm.open(config)

    var isShowAddNote by remember { mutableStateOf(false) }

    CustomDialog(isShow = isShowAddNote, setIsShow = { isShowAddNote = it }) {
        Text(text = "Add")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isShowAddNote = false
                    navigateToRoute(navController, "NoteDetail")
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = Icons.Outlined.MailOutline, contentDescription = "Text")
            Text(text = "Text")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isShowAddNote = false
                    navigateToRoute(navController, "NoteDetail")
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = Icons.Outlined.MailOutline, contentDescription = "List")
            Text(text = "List")
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    items.forEach { item ->
                        IconButton(
                            onClick = {
                                navigateToRoute(navController, item.route)
                            }) {
                            Icon(item.icon, contentDescription = "")
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { isShowAddNote = true },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Outlined.Add, "Localized description")
                    }
                }
            )


//            BottomNavigation {
//                val navBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentDestination = navBackStackEntry?.destination
//                items.forEach { screen ->
//                    BottomNavigationItem(
//                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
//                        label = { Text(stringResource(screen.resourceId)) },
//                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                        onClick = {
//                            navController.navigate(screen.route) {
//                                // Pop up to the start destination of the graph to
//                                // avoid building up a large stack of destinations
//                                // on the back stack as users select items
//                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                // Avoid multiple copies of the same destination when
//                                // reselecting the same item
//                                launchSingleTop = true
//                                // Restore state when reselecting a previously selected item
//                                restoreState = true
//                            }
//                        }
//                    )
//                }
//            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomAppBarButton.Note.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomAppBarButton.Note.route) {
                Note(
                    navController = navController,
                    realm = realm
                )
            }
            composable(BottomAppBarButton.Calendar.route) { Calendar(navController) }
            composable("noteDetail") { NoteDetail(realm = realm) }
        }
    }
}