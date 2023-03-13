package com.github.freeman.bootcamp.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import java.util.function.Consumer

/**
 *  Interface able to authenticate someone
 */
interface Authenticator {
    /**
     * Build a sign in intent and launches it using a given launcher
     *
     * @param signInLauncher A launcher for a previously-prepared call to start the process
     */
    fun signIn(signInLauncher : ActivityResultLauncher<Intent>)

    /**
     * Deletes the user currently authenticated on the phone
     *
     *@param context the context containing  global information about an application environment
     *@param onComple the callback to call on completion
     */
    fun delete(context: Context?, onComplete: Runnable?)

    /**
     * signs out the current user
     *
     * @param context the context with application-specific resources and classes, as well as up-calls for application-level operations such as launching activities, broadcasting and receiving intents, etc.
     * @param onComplete the callback to call on completion
     */
     fun signOut(context: Context?,onComplete: Runnable?)

    /**
     * Manages the result of the sign in intent
     *
     * @param result the encoded response
     * @param onSuccess the callback to call on success
     * @param onFailure the callback to call on failure
     */
    fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult?,
        onSuccess: Consumer<String?>?,
        onFailure: Consumer<String?>?
     )

}