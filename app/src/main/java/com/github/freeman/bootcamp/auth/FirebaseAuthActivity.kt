package com.github.freeman.bootcamp.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.databaseGet
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.profileExists
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


/**
 * Activity responsible for the sign in process
 */
class FirebaseAuthActivity : ComponentActivity() {

    private lateinit var authenticator: Authenticator
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private var signedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInLauncher = registerForActivityResult (
            FirebaseAuthUIActivityResultContract()
        ) { res ->
            authenticator.onSignInResult(
                res,
                {
                    signedIn = true
                },
                {}
            )
        }

        setContent {
            val signInInfo = remember { mutableStateOf("") }

            authenticator = GoogleAuthenticator()
            val currentUser = remember { mutableStateOf( FirebaseAuth.getInstance().currentUser) }

            signInInfo.value = if (currentUser.value == null) {
                "Not signed in"
            } else {
                if (currentUser.value!!.isAnonymous) {
                    "Signed in anonymously"
                } else {
                    "Signed in as : " + currentUser.value!!.email
                }
            }


            if (signedIn) {
                val dbRef = Firebase.database.reference
                val storageRef = Firebase.storage.reference
                val userId = Firebase.auth.currentUser?.uid
                val context = LocalContext.current
                currentUser.value = Firebase.auth.currentUser

                dbRef.child("profiles/$userId/username").get().addOnCompleteListener {

                    val user = FirebaseAuth.getInstance().currentUser
                    val email = user?.email

                    if (it.result.value == "" || it.result.value == null) {

                        // username + email
                        if (user != null) {
                            dbRef.child("profiles/$userId/username").setValue(user.displayName)
                        }
                        dbRef.child("profiles/$userId/email").setValue(email)

                        // default profile picture
                        val picBitmap = BitmapFactory.decodeResource(
                            context.resources,
                            R.raw.default_profile_pic
                        )

                        val stream = ByteArrayOutputStream()
                        picBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                        val image = stream.toByteArray()
                        storageRef.child("profiles/$userId/picture/pic.jpg").putBytes(image)


                    }
                }
            }



            BootcampComposeTheme {
                AuthenticationForm(
                    signInInfo = signInInfo,
                    currentUser = currentUser
                )
            }
        }


    }


    /**
     * Deletes the 'Guess It!' account from the device
     */
    fun deleteAccount(signInInfo: MutableState<String>, onDeleted: () -> Unit = {}) {
        //delete profile from 'Realtime Database' Firebase
        val uid = Firebase.auth.currentUser?.uid
        val dbrefProfile = Firebase.database.getReference("profiles/$uid")
        dbrefProfile.removeValue()

        //delete profile pic from 'Storage' Firebase
        val stgref = Firebase.storage.getReference("profiles/$uid/picture/pic.jpg")
        stgref.delete()

        //delete account from 'Authentication' Firebase
        authenticator.delete(this) {
            signInInfo.value = "Account deleted"
            onDeleted()
        }
    }

    /**
     * Signs into the google account
     */
    fun signIntoGoogleAccount(signInInfo: MutableState<String>) {
        deleteAccount(signInInfo) {
            authenticator.signIn(signInLauncher)
        }
    }

    /**
     * Signs out of the google account
     */
    fun signOutOfGoogleAccount(context: Context, signInInfo: MutableState<String>, currentUser: MutableState<FirebaseUser?>) {
        authenticator.signOut(this) {
            signInInfo.value = "Signed out"
            signInAnonymously(context, currentUser)
            signedIn = false

        }
    }

    /**
     * Signs in with anonymous account
     */
    fun signInAnonymously(context: Context, currentUser: MutableState<FirebaseUser?>) {
        Firebase.auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val userId = Firebase.auth.uid.toString()

                val dbRef = Firebase.database.reference
                val storageRef = Firebase.storage.reference

                // username
                dbRef.child("profiles/$userId/username").setValue("Guest")

                // default profile picture
                val profilePicBitmap = BitmapFactory.decodeResource(
                    context.resources,
                    R.raw.default_profile_pic
                )
                val stream = ByteArrayOutputStream()
                profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val image = stream.toByteArray()
                storageRef.child("profiles/$userId/picture/pic.jpg").putBytes(image)

                currentUser.value = FirebaseAuth.getInstance().currentUser
                signedIn = false

//                val activity = (context as? Activity)
//                activity?.finish()
            }
        }
    }
}

@Composable
fun AuthenticationForm(signInInfo: MutableState<String>, currentUser: MutableState<FirebaseUser?>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("sign_in_info"),
            text = signInInfo.value,
        )

        //currentUser.value == null
        if (currentUser.value == null) {
            // if the user is not authenticated

            ElevatedButton(
                modifier = Modifier.testTag("google_sign_in_button"),
                onClick = {
                    (context as? FirebaseAuthActivity)?.signIntoGoogleAccount(signInInfo)
                })
            { Text("Sign in with Google") }


        } else {
            //currentUser.value!!.isAnonymous
            if (currentUser.value!!.isAnonymous || signInInfo.value == "Account deleted" || signInInfo.value == "Signed out") {
                // if the user is authenticated anonymously

                ElevatedButton(
                    modifier = Modifier.testTag("google_sign_in_button"),
                    onClick = {
                        (context as? FirebaseAuthActivity)?.signIntoGoogleAccount(signInInfo)
                    })
                { Text("Sign in with Google") }
            }

            //!currentUser.value!!.isAnonymous
            else if (!currentUser.value!!.isAnonymous) {
                // if the user is authenticated with google

                ElevatedButton(
                    modifier = Modifier.testTag("sign_out_button"),
                    onClick = {
                        (context as? FirebaseAuthActivity)?.signOutOfGoogleAccount(context, signInInfo, currentUser)

                    })
                { Text("Sign out from Google authentication") }

                Spacer(modifier = Modifier.size(24.dp))

                ElevatedButton(
                    modifier = Modifier.testTag("delete_button"),
                    onClick = {
                        (context as? FirebaseAuthActivity)?.deleteAccount(signInInfo) {
                            (context as? FirebaseAuthActivity)?.signInAnonymously(context, currentUser)

//                            val activity = (context as? Activity)
//                            activity?.finish()
                        }
                    })
                { Text("Delete \'Guess It!\' account") }
            }
        }
    }
}