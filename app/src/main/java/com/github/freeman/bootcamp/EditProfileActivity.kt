package com.github.freeman.bootcamp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.EditProfileActivity.Companion.DONE
import com.github.freeman.bootcamp.EditProfileActivity.Companion.EMPTY_ERROR
import com.github.freeman.bootcamp.EditProfileActivity.Companion.ENTER_VALUE
import com.github.freeman.bootcamp.EditProfileActivity.Companion.SET_VALUE
import com.github.freeman.bootcamp.EditProfileActivity.Companion.TOPBAR_TEXT
import com.github.freeman.bootcamp.EditProfileActivity.Companion.USER_NAME
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Activity where you can edit your profile information
 */
class EditProfileActivity : ComponentActivity() {
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
            val profilePicBitmap = remember { mutableStateOf<Bitmap?>(null) }

            // get name from database
            FirebaseUtilities.databaseGet(dbUserRef.child(getString(R.string.username_path)))
                .thenAccept {
                    displayName.value = it
                }

            // get User's image from firebase storage
            LaunchedEffect(Unit) {
                FirebaseUtilities.storageGet(storageUserRef.child(getString(R.string.picture_path)))
                    .thenAccept {
                        profilePicBitmap.value = it
                    }
            }

            BootcampComposeTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppbarEditProfile()
                    EditUserDetails(displayName = displayName, profilePic = profilePicBitmap)
                }

            }

        }
    }

    companion object {
        const val TOPBAR_TEXT = "Profile"
        const val SET_VALUE = "Set value"
        const val ENTER_VALUE = "Enter value"
        const val DONE = "Enter value"
        const val EMPTY_ERROR = "Field can not be empty"
        const val USER_NAME = "NAME"
    }

}

// list of editable option available global to the activity
private val editablesList: ArrayList<EditableData> = ArrayList()

@Composable
fun EditUserDetails(context: Context = LocalContext.current, displayName: MutableState<String>, profilePic: MutableState<Bitmap?>) {
    val dbRef = Firebase.database.reference
    val storageRef = Firebase.storage.reference
    val userId = Firebase.auth.currentUser?.uid
    val showNameDialog = remember { mutableStateOf(false) }

    // stores data for images chosen in phone storage
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val image = remember { mutableStateOf(byteArrayOf()) }

    // This indicates if the optionsList has data or not
    // Initially, the list is empty. So, its value is false.
    var listPrepared by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            editablesList.clear()

            // Add the data to editablesList
            prepareEditableItemsData(displayName, showNameDialog)

            listPrepared = true
        }
    }

    if (listPrepared) {
        // Edit name dialog
        if (showNameDialog.value) {
            EditDialog(
                text = displayName,
                updateData = { name ->
                    dbRef.child(context.getString(R.string.profiles_path))
                        .child(userId.toString())
                        .child(context.getString(R.string.username_path))
                        .setValue(name)
                },
                show = showNameDialog
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("editUserDetails"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {


                if (profilePic.value != null) {

                    // Enables choosing an image in the phone storage and sens it to the database
                    val launcher = rememberLauncherForActivityResult(contract =
                    ActivityResultContracts.GetContent()) { uri: Uri? ->
                        imageUri = uri
                        image.value = readBytes(context, imageUri!!)!!
                        val uploadTask = storageRef
                            .child(context.getString(R.string.profiles_path))
                            .child(userId.toString())
                            .child(context.getString(R.string.picture_path))
                            .putBytes(image.value)
                        uploadTask.addOnFailureListener {
                            // Handle unsuccessful uploads
                        }.addOnSuccessListener {
                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        }

                    }

                    // Actual user image
                    Image(
                        painter = rememberAsyncImagePainter(if (imageUri != null) imageUri else profilePic.value),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                }
            }

            // Show the editable options
            items(editablesList) { item ->
                EditableItemStyle(
                    item = item
                )
            }
        }
    }
}

@Composable
fun TopAppbarEditProfile(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarEditProfile"),
        title = {
            Text(
                text = TOPBAR_TEXT,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(
                modifier = Modifier
                    .testTag("appBarBack"),
                onClick = {
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

// Dialog that edits any field given in argument
@Composable
fun EditDialog(text: MutableState<String>, setValue: String = SET_VALUE, enterValue: String = ENTER_VALUE, show: MutableState<Boolean>, updateData: (String) -> Unit) {

    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf(text.value) }

    Dialog(
        onDismissRequest = { show.value = false }
    ) {
        Surface(
            modifier = Modifier.testTag("customDialog"),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = setValue,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "",
                            tint = colorResource(android.R.color.darker_gray),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { show.value = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        modifier = Modifier
                            .testTag("dialogTextField")
                            .fillMaxWidth()
                            .border(
                                BorderStroke(
                                    width = 2.dp,
                                    color = colorResource(
                                        id = if (txtFieldError.value.isEmpty()) android.R.color.holo_green_light else android.R.color.holo_red_dark
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_green_light),
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                            )
                        },
                        placeholder = { Text(text = enterValue) },
                        value = txtField.value,
                        onValueChange = {
                            txtField.value = it
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                        Button(
                            onClick = {
                                if (txtField.value.isEmpty()) {
                                    txtFieldError.value = EMPTY_ERROR
                                    return@Button
                                }
                                updateData(txtField.value)
                                text.value = txtField.value
                                show.value = false
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("doneButton")
                        ) {
                            Text(text = DONE)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableItemStyle(item: EditableData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true) {
                item.clickAction()
            }
            .padding(all = 16.dp)
            .testTag("editOptionsItemStyle"),
        verticalAlignment = Alignment.CenterVertically
    ) {

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

                // editable data
                Text(
                    text = item.subTitle.value,
                    style = TextStyle(
                        fontSize = 18.sp,
                        letterSpacing = (0.8).sp,
                        color = Color.Gray
                    )
                )

            }

            // Edit icon
            Icon(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                imageVector = Icons.Outlined.Edit,
                contentDescription = item.title,
                tint = Color.Black.copy(alpha = 0.70f)
            )
        }
    }
}

/**
 * Fills the global editable options list with appropriate options
 *
 * @param displayName name of the user
 * @param showNameDialog the state of the dialog that edits the name
 */
private fun prepareEditableItemsData(displayName: MutableState<String>, showNameDialog: MutableState<Boolean>) {

    val appIcons = Icons.Rounded

    editablesList.add(
        EditableData(
            icon = appIcons.Person,
            title = USER_NAME,
            subTitle = displayName,
            clickAction = {
                showNameDialog.value = true
            }
        )
    )

}

/**
 * Converts a URI into an array of bytes
 * @param context current context
 * @param uri URI to be converted
 */
@SuppressLint("Recycle")
@Throws(IOException::class)
private fun readBytes(context: Context, uri: Uri): ByteArray? =
    context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }

/**
 * Represents an editable option
 * @param icon icon that represents the field
 * @param title main title of the field
 * @param subTitle current editable data contained in the field
 * @param clickAction what to do when the field is clicked
 */
data class EditableData(val icon: ImageVector, val title: String, val subTitle: MutableState<String>, val clickAction: () -> Unit)
