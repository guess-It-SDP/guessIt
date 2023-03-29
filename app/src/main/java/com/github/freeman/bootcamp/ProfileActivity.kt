package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

/**
 * Activity that shows up the settings and your profile
 */
class SettingsProfileActivity : ComponentActivity() {

    // actualizes the data by restarting the activity
    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val dbRef = Firebase.database.reference
            val storageRef = Firebase.storage.reference

            val displayName = remember { mutableStateOf("") }
            val profilePicBitmap = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }


            // get name from database
            val future = CompletableFuture<String>()
            //TODO get name from real database location
            dbRef.child("displayName").get().addOnSuccessListener {
                if (it.value == null) future.completeExceptionally(NoSuchFieldException())
                else future.complete(it.value as String)
            }.addOnFailureListener {
                future.completeExceptionally(it)
            }
            future.thenAccept {
                displayName.value = it
            }

            // get User's image from firebase storage
            //TODO modularize + adapt picture path
            val userRef = storageRef.child("images/cat.jpg")
            LaunchedEffect(Unit) {
                val ONE_MEGABYTE: Long = 1024 * 1024
                userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    profilePicBitmap.value = BitmapFactory.decodeByteArray(it, 0, it.size)
                }
            }

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppbarSettings(context = context)
                    Profile(displayName = displayName, profilePic = profilePicBitmap)
                }
            }

        }

    }

}

// list of option available global to the activity
private val optionsList: ArrayList<OptionsData> = ArrayList()


@Composable
fun TopAppbarSettings(context: Context) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarProfile"),
        title = {
            Text(
                text = "Settings",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}

@Composable
fun Profile(context: Context = LocalContext.current, displayName: MutableState<String>, profilePic: MutableState<Bitmap?>) {

    // This indicates if the optionsList has data or not
    // Initially, the list is empty. So, its value is false.
    var listPrepared by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            optionsList.clear()

            // Add the data to optionsList
            prepareOptionsData(context)

            listPrepared = true
        }
    }

    if (listPrepared) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("profile")
        ) {

            item {
                UserDetails(displayName = displayName, profilePic = profilePic)
            }

            // Show all the available options
            items(optionsList) { item ->
                OptionsItemStyle(item = item)
            }
        }
    }
}

@Composable
private fun UserDetails(context: Context = LocalContext.current, displayName: MutableState<String>, profilePic: MutableState<Bitmap?>) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("userDetails"),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (profilePic.value != null) {
            Image(
                painter = rememberAsyncImagePainter(profilePic.value),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // User's name
                Text(
                    text = displayName.value,
                    style = TextStyle(
                        fontSize = 22.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // User's email
                Text(
                    text = "email123@email.com",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Edit button
            IconButton(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                onClick = {
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Details",
                    tint = MaterialTheme.colors.primary
                )
            }

        }
    }
}

@Composable
private fun OptionsItemStyle(item: OptionsData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true) {
                item.clickAction()
            }
            .padding(all = 16.dp)
            .testTag("optionsItemStyle"),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Option icon
        Icon(
            modifier = Modifier
                .size(32.dp),
            imageVector = item.icon,
            contentDescription = item.title,
            tint = MaterialTheme.colors.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // Title
                Text(
                    text = item.title,
                    style = TextStyle(
                        fontSize = 18.sp,
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Subtitle
                Text(
                    text = item.subTitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray
                    )
                )

            }

            // Right arrow icon
            Icon(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = item.title,
                tint = Color.Black.copy(alpha = 0.70f)
            )
        }
    }
}

/**
 * Fills the global option list with appropriate options
 *
 * @param context the current context
 */
private fun prepareOptionsData(context: Context) {

    val appIcons = Icons.Rounded


    optionsList.add(
        OptionsData(
            icon = appIcons.PlayArrow,
            title = "Game Stats",
            subTitle = "Check your Game statistics",
            clickAction = {  } //TODO
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Settings,
            title = "Parameters",
            subTitle = "App parameters",
            clickAction = {
                context.startActivity(
                    Intent(
                        context,
                        SettingsActivity::class.java
                    )
                )
            }
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Info,
            title = "Help",
            subTitle = "Get some help about how the app works",
            clickAction = {  } //TODO
        )
    )

}

/**
 * Represents a Setting option
 */
data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String, val clickAction: () -> Unit)


@Preview
@Composable
fun TopAppBarPreview() {
    TopAppbarSettings(LocalContext.current)
}
