package com.github.freeman.bootcamp.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import java.util.function.Consumer

/**
 * Class that handles the Google sign in process
 */
class GoogleAuthenticator : Authenticator {

    companion object {
        const val LOGIN_ERROR = "login error"
        const val NULL_USER_ERROR = "User is null"
        const val CANCEL_ERROR = "User cancelled sign in"
        const val OTHER_ERROR = "login error:"
    }

    /**
     * Creates a sign in intent and launches it using the given launcher
     *
     * @param signInLauncher the launcher to use
     */
    private fun createSignInIntent(signInLauncher: ActivityResultLauncher<Intent>) {
        // Choose authentication providers
        val providers = listOf(
            GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    override fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult?,
        onSuccess: Consumer<String?>?,
        onFailure: Consumer<String?>?
    ) {
        if (result == null) {
            onFailure!!.accept(LOGIN_ERROR)
        } else if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException(NULL_USER_ERROR)
            onSuccess!!.accept(user.email)
            //context.startActivity(Intent(context, ProfileCreationActivity::class.java)
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // Sign in was cancelled by the user
            onFailure!!.accept(CANCEL_ERROR)
        } else {
            val error = result.idpResponse!!.error
            // Handle the error here
            // ...
            onFailure!!.accept("$OTHER_ERROR $error")
        }
    }

    override fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {
        createSignInIntent(signInLauncher)
    }

    override fun delete(context: Context?, onComplete: Runnable?) {
        AuthUI.getInstance()
            .delete(context!!)
            .addOnCompleteListener { onComplete!!.run() }
    }

    override fun signOut(context: Context?, onComplete: Runnable?) {
        AuthUI.getInstance()
            .signOut(context!!)
            .addOnCompleteListener { onComplete!!.run() }
    }
}