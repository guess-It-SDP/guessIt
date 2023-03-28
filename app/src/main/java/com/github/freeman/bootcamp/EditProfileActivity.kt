package com.github.freeman.bootcamp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val dbRef = Firebase.database.reference
            val displayName = remember { mutableStateOf("wow") }
            //val profilePicBitmap = remember { mutableStateOf<Bitmap?>(null) }


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

            BootcampComposeTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppbarEditProfile(context = context)
                    EditUserDetails(displayName = displayName)
                }

            }

        }
    }

}

private val editablesList: ArrayList<EditableData> = ArrayList()

@Composable
fun EditUserDetails(displayName: MutableState<String>) {
    val storageRef = Firebase.storage.reference
    val dbRef = Firebase.database.reference
    val showNameDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // This indicates if the optionsList has data or not
    // Initially, the list is empty. So, its value is false.
    var listPrepared by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            editablesList.clear()

            // Add the data to optionsList
            prepareEditableItemsData(displayName, showNameDialog)

            listPrepared = true
        }
    }

    if (listPrepared) {
        if (showNameDialog.value) {
            EditDialog(
                text = displayName,
                updateData = { name ->
                    dbRef.child("displayName").setValue(name) //TODO set in correct database
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

                //TODO modularize + adapt picture path
                val userRef = storageRef.child("images/cat.jpg")

                val bitmap = remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }

                LaunchedEffect(Unit) {
                    val ONE_MEGABYTE: Long = 1024 * 1024
                    userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                        bitmap.value = BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                }

                if (bitmap.value != null) {
                    var imageUri by remember { mutableStateOf<Uri?>(null) }
                    val image = remember { mutableStateOf(byteArrayOf()) }

                    val launcher = rememberLauncherForActivityResult(contract =
                    ActivityResultContracts.GetContent()) { uri: Uri? ->
                        val stream = ByteArrayOutputStream()
                        bitmap.value!!.compress(Bitmap.CompressFormat.PNG, 90, stream)
                        image.value = stream.toByteArray()
                        imageUri = uri
                        image.value = readBytes(context, imageUri!!)!!
                        val uploadTask = userRef.putBytes(image.value)
                        uploadTask.addOnFailureListener {
                            // Handle unsuccessful uploads
                        }.addOnSuccessListener { taskSnapshot ->
                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                            // ...
                        }

                    }

                    Image(
                        painter = rememberAsyncImagePainter(if (imageUri != null) imageUri else bitmap.value),
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

            // Show the options
            items(editablesList) { item ->
                EditableItemStyle(
                    item = item
                )
            }
        }
    }



}

@Composable
fun TopAppbarEditProfile(context: Context) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarProfile"),
        title = {
            Text(
                text = "Profile",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                Toast.makeText(context, "Nav Button", Toast.LENGTH_SHORT).show()
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
private fun EditDialog(text: MutableState<String>, updateData: (String) -> Unit, show: MutableState<Boolean>) {

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
                            text = "Set value",
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
                        placeholder = { Text(text = "Enter value") },
                        value = txtField.value,
                        //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            txtField.value = it
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                        Button(
                            onClick = {
                                if (txtField.value.isEmpty()) {
                                    txtFieldError.value = "Field can not be empty"
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
                        ) {
                            Text(text = "Done")
                        }
                    }
                }
            }
        }
    }
}


// Row style for editable items
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

        // Icon
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

private fun prepareEditableItemsData(displayName: MutableState<String>, showNameDialog: MutableState<Boolean>) {

    val appIcons = Icons.Rounded
    //displayName.value = "Chris P. Bacon"

    editablesList.add(
        EditableData(
            icon = appIcons.Person,
            title = "Name",
            subTitle = displayName,
            clickAction = {
                showNameDialog.value = true
            }
        )
    )

}

//TODO add this to a utility file or something
@SuppressLint("Recycle")
@Throws(IOException::class)
private fun readBytes(context: Context, uri: Uri): ByteArray? =
    context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }

data class EditableData(val icon: ImageVector, val title: String, val subTitle: MutableState<String>, val clickAction: () -> Unit)
