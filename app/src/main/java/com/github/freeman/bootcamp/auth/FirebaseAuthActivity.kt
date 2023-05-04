package com.github.freeman.bootcamp.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.ACCOUNT_DELETED_INFO
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.CANCEL_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.DELETE_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.DELETION_WARNING_TEXT
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.DELETION_WARNING_TITLE
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.GOOGLE_SIGN_IN_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.GOOGLE_SIGN_OUT_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.PROFILE_DELETION_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.SCREEN_TITLE
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.SIGNED_OUT_INFO
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities.createProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


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
                NOT_SIGNED_IN_INFO
            } else {
                if (currentUser.value!!.isAnonymous) {
                    ANONYMOUSLY_SIGNED_IN_INFO
                } else {
                    "$GOOGLE_SIGN_IN_INFO ${currentUser.value!!.email}"
                }
            }


            if (signedIn) {
                // Checks if a profile already exists for the current user. If not, creates one

                val dbRef = Firebase.database.reference
                val userId = Firebase.auth.currentUser?.uid
                val context = LocalContext.current
                currentUser.value = Firebase.auth.currentUser

                dbRef
                    .child(getString(R.string.profiles_path))
                    .child(userId.toString())
                    .child(getString(R.string.username_path))
                    .get()
                    .addOnCompleteListener {

                    val user = FirebaseAuth.getInstance().currentUser
                    val email = user?.email

                    // If profile doesn't exist
                    if (it.result.value == "" || it.result.value == null) {
                        createProfile(context, userId!!, user!!.displayName!!, email)
                    }
                }
            }

            BootcampComposeTheme {
                TopAppbarAccount()
                AuthenticationForm(
                    signInInfo = signInInfo,
                    currentUser = currentUser
                )
            }
        }

    }

    companion object {
        const val GOOGLE_SIGN_IN_BUTTON = "Sign in with Google"
        const val PROFILE_DELETION_BUTTON = "Delete \'Guess It!\' account"
        const val GOOGLE_SIGN_OUT_BUTTON = "Sign out from Google authentication"

        const val NOT_SIGNED_IN_INFO = "Not signed in"
        const val ANONYMOUSLY_SIGNED_IN_INFO = "Signed in anonymously"
        const val GOOGLE_SIGN_IN_INFO = "Signed in as :"
        const val ACCOUNT_DELETED_INFO = "Account deleted"
        const val SIGNED_OUT_INFO = "Signed out"

        const val DEFAULT_NAME = "Guest"
        const val SCREEN_TITLE = "Account"

        const val DELETE_BUTTON = "Delete"
        const val CANCEL_BUTTON = "Cancel"
        const val DELETION_WARNING_TITLE = "Warning"
        const val DELETION_WARNING_TEXT = "This action will completely delete your account with all data of your previous games.\n\nAre you sure you want to continue?"
    }

    /**
     * Deletes the 'Guess It!' account from the device
     */
    fun deleteAccount(signInInfo: MutableState<String>, onDeleted: () -> Unit = {}) {

        //delete profile from 'Realtime Database' Firebase
        val uid = Firebase.auth.currentUser?.uid
        val dbrefProfile = Firebase.database.reference
            .child(getString(R.string.profiles_path))
            .child(uid.toString())
        dbrefProfile.removeValue()

        //delete profile pic from 'Storage' Firebase
        val stgref = Firebase.storage.reference
            .child(getString(R.string.profiles_path))
            .child(uid.toString())
            .child(getString(R.string.picture_path))
        stgref.delete()

        //delete account from 'Authentication' Firebase
        authenticator.delete(this) {
            signInInfo.value = ACCOUNT_DELETED_INFO
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
            signInInfo.value = SIGNED_OUT_INFO
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

                createProfile(context, userId, DEFAULT_NAME)

                currentUser.value = FirebaseAuth.getInstance().currentUser
                signedIn = false

            }
        }
    }
}

@Composable
fun TopAppbarAccount(context: Context = LocalContext.current) {

    TopAppBar(
        modifier = Modifier.testTag("topAppbarAccount"),
        title = {
            androidx.compose.material.Text(
                text = SCREEN_TITLE,
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

/**
 * An alert which pops up when a user clicks to the account deletion button to be sure he/she want to continue
 * @param signInInfo the sign in info (authenticated as, not signed in, etc) which will be displayed on the screen
 * @param currentUser the current user of the app
 * @param show true if the warning need to be shown, becomes false when it needs to be closed
 */
@Composable
fun WarningDeletion(signInInfo: MutableState<String>, currentUser: MutableState<FirebaseUser?>, show: MutableState<Boolean>) {
    val context = LocalContext.current

    AlertDialog(
        modifier = Modifier.testTag("deletionAlertDialog"),
        title = {
            Text(
                text = DELETION_WARNING_TITLE,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(DELETION_WARNING_TEXT)
        },
        onDismissRequest = {
            show.value = false },
        confirmButton = {
            Button(
                onClick = {
                    (context as? FirebaseAuthActivity)?.deleteAccount(signInInfo) {
                        (context as? FirebaseAuthActivity)?.signInAnonymously(context, currentUser)
                    }
                    show.value = false
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .testTag("deleteButton")
            ) {
                Text(
                    text = DELETE_BUTTON,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    show.value = false
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .testTag("cancelButton")
            ) {
                Text(
                    text = CANCEL_BUTTON,
                    color = Color.White
                )
            }
        }
    )
}

/**
 * Display authentication info and authentication buttons depending on the user authentication state
 * @param signInInfo the sign in info that will be displayed
 * @param currentUser the current user, can be anonymous
 */
@Composable
fun AuthenticationForm(signInInfo: MutableState<String>, currentUser: MutableState<FirebaseUser?>) {
    val context = LocalContext.current
    val alertOpen = remember { mutableStateOf(false) }

    if (alertOpen.value) {
        WarningDeletion(signInInfo, currentUser, alertOpen)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("sign_in_info"),
            text = signInInfo.value,
        )

        if (currentUser.value == null) {
            // if the user is not authenticated

            ElevatedButton(
                modifier = Modifier.testTag("google_sign_in_button"),
                onClick = {
                    (context as? FirebaseAuthActivity)?.signIntoGoogleAccount(signInInfo)
                })
            { Text(GOOGLE_SIGN_IN_BUTTON) }


        } else {
            if (currentUser.value!!.isAnonymous || signInInfo.value == ACCOUNT_DELETED_INFO || signInInfo.value == SIGNED_OUT_INFO) {
                // if the user is authenticated anonymously

                ElevatedButton(
                    modifier = Modifier.testTag("google_sign_in_button"),
                    onClick = {
                        (context as? FirebaseAuthActivity)?.signIntoGoogleAccount(signInInfo)
                    })
                { Text(GOOGLE_SIGN_IN_BUTTON) }
            }

            else if (!currentUser.value!!.isAnonymous) {
                // if the user is authenticated with google

                ElevatedButton(
                    modifier = Modifier.testTag("sign_out_button"),
                    onClick = {
                        (context as? FirebaseAuthActivity)?.signOutOfGoogleAccount(context, signInInfo, currentUser)

                    })
                { Text(GOOGLE_SIGN_OUT_BUTTON) }

                Spacer(modifier = Modifier.size(24.dp))

                ElevatedButton(
                    modifier = Modifier.testTag("delete_button"),
                    onClick = {
                        alertOpen.value = true
                    })
                { Text(PROFILE_DELETION_BUTTON) }
            }
        }
    }
}