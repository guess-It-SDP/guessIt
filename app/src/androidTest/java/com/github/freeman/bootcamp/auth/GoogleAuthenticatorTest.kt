package com.github.freeman.bootcamp.auth

import android.app.Activity
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.data.model.User
import com.github.freeman.bootcamp.auth.GoogleAuthenticator.Companion.CANCEL_ERROR
import com.github.freeman.bootcamp.auth.GoogleAuthenticator.Companion.NULL_USER_ERROR
import com.github.freeman.bootcamp.auth.GoogleAuthenticator.Companion.OTHER_ERROR
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

class GoogleAuthenticatorTest {
    @Test
    fun onSignInWithFailedResultCodeExecutesFailCallBack() {
        val realGoogleAuthenticator = GoogleAuthenticator()
        val result = AtomicReference("")
        realGoogleAuthenticator.onSignInResult(
            FirebaseAuthUIAuthenticationResult(0, null),
            { result.set("success") },
            { result.set("failure") }
        )
        MatcherAssert.assertThat(result.get(), CoreMatchers.`is`("failure"))
    }

    @Test
    fun onSignInResultThrowsIfUserIsNull() {
        val realGoogleAuthenticator = GoogleAuthenticator()
        val error: Exception = Assert.assertThrows(
            IllegalStateException::class.java
        ) {
            realGoogleAuthenticator.onSignInResult(
                FirebaseAuthUIAuthenticationResult(Activity.RESULT_OK, null),
                {},
                {}
            )
        }
        MatcherAssert.assertThat(error.message, CoreMatchers.`is`(NULL_USER_ERROR))
    }

    @Test
    fun onSignResultCallsOnFailureWhenUserCancelsSignIn() {
        val realGoogleAuthenticator = GoogleAuthenticator()

        var onFailureMsg = ""

        realGoogleAuthenticator.onSignInResult(
            FirebaseAuthUIAuthenticationResult(Activity.RESULT_CANCELED, null),
            {onFailureMsg = "success"},
            {errorMsg -> onFailureMsg = errorMsg?:"errorMsg is null"}
        )

        MatcherAssert.assertThat(onFailureMsg, CoreMatchers.`is`(CANCEL_ERROR))
    }

    @Test
    fun onSignInResultCallsOnFailureWhenResultCodeIsNotOkOrCanceled() {

        val response = IdpResponse.Builder(
            User.Builder("provider", "email").build())
            .setNewUser(false)
            .setSecret("secret")
            .setToken("token")
            .setPendingCredential(null)
            .build()

        val realGoogleAuthenticator = GoogleAuthenticator()

        var onFailureMsg = ""

        realGoogleAuthenticator.onSignInResult(
            FirebaseAuthUIAuthenticationResult(1, response),
            {onFailureMsg = "success"},
            {errorMsg -> onFailureMsg = errorMsg ?: "errorMsg is null"}
        )

        MatcherAssert.assertThat(onFailureMsg, CoreMatchers.`is`("$OTHER_ERROR null"))
    }
}