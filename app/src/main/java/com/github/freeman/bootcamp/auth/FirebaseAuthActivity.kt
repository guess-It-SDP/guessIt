package com.github.freeman.bootcamp.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.ANONYMOUS_SIGN_IN_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.GOOGLE_SIGN_IN_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.GOOGLE_SIGN_OUT_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.PROFILE_CREATION_BUTTON
import com.github.freeman.bootcamp.auth.FirebaseAuthActivity.Companion.PROFILE_DELETION_BUTTON
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


/**
 * Activity responsible for the sign in process
 */
class FirebaseAuthActivity : ComponentActivity() {

    private var signInInfo: String by mutableStateOf("")
    private lateinit var authenticator: Authenticator
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticator = GoogleAuthenticator()
        val currentUser = FirebaseAuth.getInstance().currentUser
        signInInfo = if (currentUser == null) {
            NOT_SIGNED_IN_INFO
        } else {
            if (currentUser.isAnonymous) {
                ANONYMOUSLY_SIGNED_IN_INFO
            } else {
                "$GOOGLE_SIGN_IN_INFO ${currentUser.email}"
            }
        }
        signInLauncher = registerForActivityResult (
            FirebaseAuthUIActivityResultContract()
        ) { res ->
            authenticator.onSignInResult(
                res,
                { email -> signInInfo = "$GOOGLE_SIGN_IN_INFO $email" },
                { errorMsg -> signInInfo = errorMsg.toString() }

            )
        }

        setContent {
            BootcampComposeTheme {
                AuthenticationForm(
                    signInInfo = this.signInInfo
                )
            }
        }

    }

    companion object {
        const val GOOGLE_SIGN_IN_BUTTON = "Sign in with Google"
        const val ANONYMOUS_SIGN_IN_BUTTON = "Sign in as guest"
        const val PROFILE_CREATION_BUTTON = "Create \'Guess It!\' profile"
        const val PROFILE_DELETION_BUTTON = "Delete \'Guess It!\' account"
        const val GOOGLE_SIGN_OUT_BUTTON = "Sign out from Google authentication"

        const val NOT_SIGNED_IN_INFO = "Not signed in"
        const val ANONYMOUSLY_SIGNED_IN_INFO = "Signed in anonymously"
        const val GOOGLE_SIGN_IN_INFO = "Signed in as :"
        const val ACCOUNT_DELETED_INFO = "Account deleted"
        const val SIGNED_OUT_INFO = "Signed out"

        const val AUTH_FAILURE_TOAST = "Authentication failed"
    }


    /**
     * Deletes the 'Guess It!' account from the device
     */
    fun deleteAccount() {
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
        authenticator.delete(this) { signInInfo = ACCOUNT_DELETED_INFO }
    }

    /**
     * Signs into the google account
     */
    fun signIntoGoogleAccount() {
        authenticator.signIn(signInLauncher)
    }

    /**
     * Signs out of the google account
     */
    fun signOutOfGoogleAccount() {
        authenticator.signOut(this) { signInInfo = SIGNED_OUT_INFO }
    }

    /**
     * Signs in with anonymous account
     */
    fun signInAnonymously() {
        Firebase.auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInAnonymously:success")
                signInInfo = ANONYMOUSLY_SIGNED_IN_INFO
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInAnonymously:failure", task.exception)
                Toast.makeText(baseContext, AUTH_FAILURE_TOAST, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun AuthenticationForm(signInInfo: String) {
    val context = LocalContext.current

    /* //TODO: Display "Create profile" only when there is an existing profile and "Delete profile" where there isn't
    var profileExists = false
    FirebaseUtilities.profileExists(FirebaseAuth.getInstance().currentUser, Firebase.database.reference)
        .thenAccept {
            profileExists = it
        }
     */

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.testTag("sign_in_info"),
            text = signInInfo,
        )

        if (FirebaseAuth.getInstance().currentUser == null) {
            // if the user is not authenticated

            Spacer(modifier = Modifier.size(24.dp))

            ElevatedButton(
                modifier = Modifier.testTag("google_sign_in_button"),
                onClick = {
                    (context as? FirebaseAuthActivity)?.signIntoGoogleAccount()
                })
            { Text(GOOGLE_SIGN_IN_BUTTON) }

            Spacer(modifier = Modifier.size(24.dp))

            ElevatedButton(
                modifier = Modifier.testTag("anonymous_sign_in_button"),
                onClick = {
                    (context as? FirebaseAuthActivity)?.signInAnonymously()
                })
            { Text(ANONYMOUS_SIGN_IN_BUTTON) }



        } else {
            // if the user is authenticated (with Google or anonymously)

            Spacer(modifier = Modifier.size(24.dp))


            // if the user doesn't have a 'Guess It!' account
            ElevatedButton(
                modifier = Modifier.testTag("create_profile_button"),
                onClick = {
                    context.startActivity(Intent(context, ProfileCreationActivity::class.java))
                })
            { Text(PROFILE_CREATION_BUTTON) }

            Spacer(modifier = Modifier.size(24.dp))

            // if the user has a 'Guess It!' account
            ElevatedButton(
                modifier = Modifier.testTag("delete_button"),
                onClick = {
                    (context as? FirebaseAuthActivity)?.deleteAccount()
                })
            { Text(PROFILE_DELETION_BUTTON) }



            Spacer(modifier = Modifier.size(24.dp))

            if (FirebaseAuth.getInstance().currentUser?.isAnonymous == false) {
                // if the user is authenticated with google

                ElevatedButton(
                    modifier = Modifier.testTag("sign_out_button"),
                    onClick = {
                        (context as? FirebaseAuthActivity)?.signOutOfGoogleAccount()
                    })
                { Text(GOOGLE_SIGN_OUT_BUTTON) }

                Spacer(modifier = Modifier.size(24.dp))
            }


        }

    }
}