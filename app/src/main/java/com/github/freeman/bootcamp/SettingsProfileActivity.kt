package com.github.freeman.bootcamp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
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
import androidx.compose.material.icons.rounded.AccountCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.SettingsActivity.Companion.SETTINGS_TITLE
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            val dbRef = Firebase.database.reference
            val storageRef = Firebase.storage.reference
            val userId = Firebase.auth.currentUser?.uid
            val dbUserRef = dbRef.child(getString(R.string.profiles_path))
                    .child(userId.toString())
            val storageUserRef = storageRef.child(getString(R.string.profiles_path))
                    .child(userId.toString())

            val displayName = remember { mutableStateOf("") }
            val email = remember { mutableStateOf("") }
            val profilePicBitmap = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }


            // get name from database
            FirebaseUtilities.databaseGet(dbUserRef.child(getString(R.string.username_path)))
                .thenAccept {
                    displayName.value = it ?: "Guest"
                }

            // get email from database
            FirebaseUtilities.databaseGet(dbUserRef.child(getString(R.string.email_path)))
                .thenAccept {
                    email.value = it ?: ""
                }


            // get User's image from firebase storage
            LaunchedEffect(Unit) {
                FirebaseUtilities.storageGet(storageUserRef.child(getString(R.string.picture_path)))
                    .thenAccept {
                        profilePicBitmap.value = it
                    }
            }

            BootcampComposeTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppbarSettings()
                    Profile(displayName = displayName, email = email, profilePic = profilePicBitmap)
                }
            }

        }

    }

}

// list of option available global to the activity
private val optionsList: ArrayList<OptionsData> = ArrayList()


@Composable
fun TopAppbarSettings(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarProfile"),
        title = {
            Text(
                text = SETTINGS_TITLE,
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
fun Profile(context: Context = LocalContext.current, displayName: MutableState<String>, email: MutableState<String>, profilePic: MutableState<Bitmap?>) {

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
                UserDetails(displayName = displayName, email = email, profilePic = profilePic)
            }

            // Show all the available options
            items(optionsList) { item ->
                OptionsItemStyle(item = item)
            }
        }
    }
}

@Composable
private fun UserDetails(context: Context = LocalContext.current, displayName: MutableState<String>, email: MutableState<String>, profilePic: MutableState<Bitmap?>) {

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
                    text = email.value,
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
                    .weight(weight = 1f, fill = false)
                    .testTag("editProfileButton"),
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
            clickAction = {  }
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Settings,
            title = "Parameters",
            subTitle = "App parameters",
            clickAction = {
                context.startActivity(Intent(context, SettingsActivity::class.java)
                )
            }
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.AccountCircle,
            title = "Manage Account",
            subTitle = "Sign in or sign out from your Google account",
            clickAction = {
                context.startActivity(Intent(context, FirebaseAuthActivity::class.java))
            }
        )
    )

    optionsList.add(
        OptionsData(
            icon = appIcons.Info,
            title = "Help",
            subTitle = "Get some help about how the app works",
            clickAction = {  }
        )
    )

}

/**
 * Represents a Setting option
 * @param icon icon that represents the field
 * @param title main title of the field
 * @param subTitle description text of the field
 * @param clickAction what to do when the field is clicked
 */
data class OptionsData(val icon: ImageVector, val title: String, val subTitle: String, val clickAction: () -> Unit)

