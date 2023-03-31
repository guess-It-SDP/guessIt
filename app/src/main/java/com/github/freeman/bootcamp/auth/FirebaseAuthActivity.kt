package com.github.freeman.bootcamp.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.freeman.bootcamp.TopicSelectionActivity
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.auth.FirebaseAuth


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
            "Not signed in"
        } else {
            "Signed in as : " + currentUser.email
        }
        signInLauncher = registerForActivityResult (
            FirebaseAuthUIActivityResultContract()
        ) { res ->
            authenticator.onSignInResult(
                res,
                { email -> signInInfo = "Signed in as : $email" },
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

    /**
     * Deletes the google account from the device
     */
    fun deleteGoogleAccount() {
        authenticator.delete(this) { signInInfo = "Account deleted" }
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
        authenticator.signOut(this) { signInInfo = "Signed out" }
    }
}

@Composable
fun AuthenticationForm(signInInfo: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // read only text field

        Text(
            modifier = Modifier.testTag("sign_in_info"),
            text = signInInfo,
        )

        ElevatedButton(
            modifier = Modifier.testTag("sign_in_button"),
            onClick = {
                (context as? FirebaseAuthActivity)?.signIntoGoogleAccount()
                if (FirebaseAuth.getInstance().currentUser != null) {
                    context.startActivity(Intent(context, ProfileCreationActivity::class.java))
                }
            })
        { Text("Sign in") }
        Spacer(modifier = Modifier.size(24.dp))
        ElevatedButton(
            modifier = Modifier.testTag("sign_out_button"),
            onClick = {
                (context as? FirebaseAuthActivity)?.signOutOfGoogleAccount()
            })
        { Text("Sign out") }
        Spacer(modifier = Modifier.size(24.dp))
        ElevatedButton(
            modifier = Modifier.testTag("delete_button"),
            onClick = {
                (context as? FirebaseAuthActivity)?.deleteGoogleAccount()
            })
        { Text("Delete account") }
    }
}