package com.github.freeman.bootcamp.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.freeman.bootcamp.auth.ui.theme.BootcampComposeTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AuthenticationActivity : ComponentActivity() {
    private lateinit var authenticator: Authenticator
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private fun toast(string: String?){
        Toast.makeText(
            this@AuthenticationActivity,
            string ,
            Toast.LENGTH_SHORT
        ).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticator = MyAuthenticator()
        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { res ->
            authenticator.onSignInResult(res,
                { email ->
                    toast(email)
                },
                { errorMsg ->
                  toast(errorMsg)
                })
        }

        setContent {
            BootcampComposeTheme {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        //buttons
                        signInButton()
                        signOutButton()
                        deleteButton()
                        seeUser()
                    }
            }
        }
    }

    /**
     *  Sign in
     */
    @Composable
    fun signInButton() {
        Button(
            modifier = Modifier.testTag("sign_in_button"),
            onClick = {
                authenticator.signIn(signInLauncher)
            }
        ) {
            Text("Sign in")
        }
    }

    /**
     *  Sign out
     */
    @Composable
    fun signOutButton() {
        Button(
            modifier = Modifier.testTag("sign_out_button"),
            onClick = {
                authenticator.signOut(this ){toast("Signed out")}
            }
        ) {
            Text("Sign out")
        }
    }

    /**
     *  delete
     */
    @Composable
    fun deleteButton() {
        Button(
            modifier = Modifier.testTag("delete_button"),
            onClick = {
                authenticator.delete(this ){toast("Deleted")}
            }
        ) {
            Text("Delete")
        }
    }

    @Composable
    fun seeUser() {
        Button(
            modifier = Modifier.testTag(" seeUser"),
            onClick = {
                toast(FirebaseAuth.getInstance().currentUser.toString())
            }
        ) {
            Text("seeUser")
        }
    }

}