package com.github.freeman.bootcamp.firebase.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.freeman.bootcamp.MainMenuActivity
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

/**
 * The activity that create the GuessIt profile in the real time database
 * linked with the google profile
 */
class ProfileCreationActivity : ComponentActivity() {
    private val dbref = Firebase.database.getReference("Profiles")
    private val storageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BootcampComposeTheme {
                ProfileCreationScreen(dbref, storageRef)
            }
        }
    }
}

/**
 * The bar where the user chooses his/her username
 */
@Composable
fun UsernameBar(
    username: String,
    onUsernameChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .testTag("usernameBar")
            .padding(8.dp)
            .fillMaxSize()
    ) {

        OutlinedTextField(
            modifier = Modifier.testTag("usernameTextField"),
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(
                modifier = Modifier.testTag("usernameLabel"),
                text = "Username") },
            placeholder = { Text(
                modifier = Modifier.testTag("usernamePlaceholder"),
                text = "Choose a username",
                color = Color.LightGray)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier.testTag("usernameOkayButton"),
            onClick = onSendClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_done_24),
                contentDescription = "okIconForUsername"
            )

        }
    }

}

/**
 * The Screen of the profile creation activity
 */
@Composable
fun ProfileCreationScreen(dbref: DatabaseReference, storageRef: StorageReference) {
    var username by remember { mutableStateOf("") }
    val context = LocalContext.current

    MaterialTheme {
        Column(
            modifier = Modifier
                .testTag("profileCreationScreen")
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            UsernameBar(
                username = username,
                onUsernameChange = { username = it },
                onSendClick = {
                    if (username.isEmpty()) {
                        username = "UnknownPlayer"
                    }
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    // email
                    val userEmail = Firebase.auth.currentUser?.email
                    dbref.child(userId!!).child("email").setValue(userEmail)

                    // username
                    dbref.child(userId).child("username").setValue(username)

                    // default profile picture
                    val profilePicBitmap = BitmapFactory.decodeResource(context.resources, R.raw.default_profile_pic)
                    val stream = ByteArrayOutputStream()
                    profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    val image = stream.toByteArray()
                    storageRef.child("Profiles/$userId/picture/pic.jpg").putBytes(image)

                    // go to main menu after profile created
                    context.startActivity(Intent(context, MainMenuActivity::class.java))
                }
            )
        }
    }
}
