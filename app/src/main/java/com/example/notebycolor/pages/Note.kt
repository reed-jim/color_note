package com.example.notebycolor.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.notebycolor.AppColor
import com.example.notebycolor.appColors
import com.example.notebycolor.database.TextNote
import com.example.notebycolor.navigateToRoute
import com.example.notebycolor.util.CustomDialog
import com.example.notebycolor.util.SlideHorizontalTransition
import com.example.notebycolor.util.bottomBorder
import com.example.notebycolor.util.colorNameToColor
import com.example.notebycolor.util.convertLongToTime
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import java.util.Date

class Option(val name: String, val icon: ImageVector)

val options = listOf(
    Option("Color", Icons.Outlined.List),
    Option("Sort", Icons.Outlined.List),
    Option("View", Icons.Outlined.List)
)

val sortOptions = listOf(
    Option("by created time", Icons.Outlined.Notifications),
    Option("by modified time", Icons.Outlined.Notifications),
    Option("alphabetically", Icons.Outlined.Notifications),
    Option("by color", Icons.Outlined.AccountBox),
    Option("by reminder", Icons.Outlined.Notifications)
)

val viewOptions = listOf(
    Option("List", Icons.Outlined.Notifications),
    Option("Details", Icons.Outlined.Notifications),
    Option("Grid", Icons.Outlined.Notifications),
    Option("Large Grid", Icons.Outlined.AccountBox)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Note(navController: NavController, realm: Realm) {
    val colorScheme = MaterialTheme.colorScheme
    val textColor = MaterialTheme.colorScheme.onBackground

    var choose by remember { mutableStateOf("Color") }

    var showDialog by remember { mutableStateOf(false) }
    var showColorSort by remember { mutableStateOf(false) }

    var sort by remember { mutableStateOf("None") }

    var textNotes by remember { mutableStateOf(listOf<TextNote>()) }

    CustomDialog(isShow = showDialog, setIsShow = { showDialog = it }) {
        Row(
            modifier = Modifier.fillMaxWidth()
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { item ->
                Button(
                    onClick = {
                        choose = item.name
                    },
                    modifier = Modifier
                        .weight(1f)
                        .drawBehind {
                            if (choose == item.name) {
                                drawLine(
                                    color = if (choose == item.name) colorScheme.primary else colorScheme.tertiary,
                                    start = Offset(x = 0f, y = size.height),
                                    end = Offset(x = size.width, y = size.height),
                                    strokeWidth = 4.dp.toPx()
                                )
                            }
                        },
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "by modified time",
                            tint = colorScheme.onSurface
                        )
                        Text(text = item.name, color = colorScheme.onSurface)
                    }
                }
            }
        }

//        Divider()

        SlideHorizontalTransition(isVisible = choose == "Color") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0..4) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (j in 0..1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 40.dp)
                                    .weight(1f)
                                    .background(color = appColors[j + 2 * i].color)
                                    .border(width = 1.dp, color = colorScheme.primary)
                                    .padding(8.dp)
                                    .clickable { },
                                horizontalArrangement = Arrangement.Center
                            ) {
//                                TextField(value = "", onValueChange = {})
                            }
//
//                            Button(
//                                onClick = {},
//                                modifier = Modifier.weight(1f),
//                                shape = RectangleShape,
//                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
//                            ) {}
                        }
                    }
                }
            }
        }

        SlideHorizontalTransition(isVisible = choose == "Sort") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sortOptions.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sort = "createdTime" }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.name)
                    }
                }
            }
        }

        SlideHorizontalTransition(isVisible = choose == "View") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewOptions.forEach { item ->
                    Row {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.name)
                    }
                }
            }
        }
    }

    CustomDialog(isShow = showColorSort, setIsShow = { showColorSort = it }) {
        for (i in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0..2) {
                    Button(
                        onClick = {},
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {}
                }
            }
        }
    }

    LaunchedEffect(key1 = sort) {
        var queryTextNotes: Flow<ResultsChange<TextNote>>?

        if (sort == "createdTime") {
            queryTextNotes =
                realm.query<TextNote>("color = $0", "Red")
                    .sort("createdTime", Sort.DESCENDING).asFlow()


        } else {
            queryTextNotes = realm.query<TextNote>().asFlow()
        }

        async {
            queryTextNotes?.collect { results ->
                when (results) {
                    is InitialResults<TextNote> -> {
                        textNotes = results.list
                    }

                    else -> {
                        // do nothing on changes
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondary)
                .clickable { showDialog = true }
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sort by",
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        textNotes.forEach { item ->
            Row(
                modifier = Modifier
                    .background(color = colorNameToColor(item.color).copy(0.2f))
                    .drawBehind
                    {
                        drawLine(
                            color = colorNameToColor(item.color),
                            start = Offset(4.dp.toPx(), 0f),
                            end = Offset(4.dp.toPx(), size.height),
                            strokeWidth = 8.dp.toPx()
                        )
                    }
                    .clickable {
                        navigateToRoute(navController, "noteDetail")
                    }
                    .padding(16.dp)
            ) {
                Text(text = item.text, color = textColor)
                Spacer(Modifier.weight(1f))
                Icon(imageVector = Icons.Outlined.Check, contentDescription = "")
                Text(text = convertLongToTime(item.createdTime.epochSeconds), color = textColor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetail(realm: Realm) {
    var inputText by rememberSaveable { mutableStateOf("") }
    var inputColor by rememberSaveable { mutableStateOf("Red") }

    var isShowDropdown by remember { mutableStateOf(false) }

    fun createNote() {
        realm.writeBlocking {
            copyToRealm(TextNote().apply {
                text = inputText
                color = "Red"
                createdTime = RealmInstant.now()
                modifiedTime = RealmInstant.now()
            })
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "ArrowBack")
            Text(
                text = "Edit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = colorNameToColor(inputColor))
                    .clickable {
                        isShowDropdown = true
                    }
            ) {
                DropdownMenu(
                    expanded = isShowDropdown,
                    onDismissRequest = { isShowDropdown = false }) {
                    for (i in 0..2) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (j in 0..2) {
                                Row(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(color = appColors[j + 3 * i].color)
                                        .clickable { inputColor = appColors[j + 3 * i].name }
                                ) {

                                }
                            }
                        }
                    }
                }
            }
            Text(text = "Date")
        }

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text("Enter your note here...") },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { createNote() }) {
                Text(text = "Save")
            }
//            FloatingActionButton(onClick = { /*TODO*/ }) {
//                Icon(imageVector = Icons.Outlined.Check, contentDescription = "Save")
//            }
        }
    }
}