package com.github.freeman.bootcamp.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class MyAuthenticator : Authenticator {

    /**
     * create the sign-in intent from Google
     */
    private fun signInIntent(signInLauncher: ActivityResultLauncher<Intent>) {
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()

        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }


    override fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {
       signInIntent(signInLauncher)
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

    override fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult?,
        onSuccess: java.util.function.Consumer<String?>?,
        onFailure: java.util.function.Consumer<String?>?
    ) {
        if (result == null) {
            onFailure!!.accept("login error")
        } else if (result.resultCode == Activity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException("User is null")
            onSuccess!!.accept(user.email)
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            onFailure!!.accept("cancelled sign in, you are probably already connected")
        } else {
            val error = result.idpResponse!!.error
            onFailure!!.accept("login error: $error")
        }
    }
}
