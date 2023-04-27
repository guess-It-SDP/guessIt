package com.github.freeman.bootcamp

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.chat.Main
import com.github.freeman.bootcamp.utilities.firebase.FirebaseSingletons.database
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun initDataBase(current: Context): DatabaseReference {
        val chatId = "testchatid01"
        FirebaseEmulator.init()
        return database.get().database.reference
            .child(current.getString(R.string.chat_path))
            .child(chatId)
    }

    @Before
    fun init() {
        composeRule.setContent {
            val dbref = initDataBase(LocalContext.current)
            BootcampComposeTheme {
                Main(dbref)
            }
        }
    }

    @Test
    fun activateChatButtonIsDisplayed() {
        composeRule.onNodeWithTag("chatActivateButton").assertIsDisplayed()
    }


    @Test
    fun chatScreenIsNotDisplayedBeforeActivation() {
        composeRule.onNodeWithTag("chatScreen").assertDoesNotExist()
    }

    @Test
    fun chatScreenIsDisplayedAfterActivation() {
        composeRule.onNodeWithTag("chatActivateButton").performClick()
        composeRule.onNodeWithTag("chatScreen").assertIsDisplayed()
    }

    @Test
    fun bottomBarIsDisplayedAfterActivation() {
        composeRule.onNodeWithTag("chatActivateButton").performClick()
        composeRule.onNodeWithTag("chatBottomBar").assertIsDisplayed()
    }

    @Test
    fun backGroundComposableIsDisplayed() {
        composeRule.onNodeWithTag("backGroundComposable").assertIsDisplayed()
        composeRule.onNodeWithTag("chatActivateButton").performClick()
        composeRule.onNodeWithTag("backGroundComposable").assertIsDisplayed()
    }

    @Test
    fun backButtonDeactivatesChat() {
        composeRule.onNodeWithTag("chatActivateButton").performClick()
        composeRule.onNodeWithTag("chatScreen").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithTag("chatScreen").assertDoesNotExist()

    }

    @Test
    fun sendingMessageDisplaysInChat() {
        composeRule.onNodeWithTag("chatActivateButton").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Bonjour Monde !")
        composeRule.onNodeWithTag("chatSendButton").performClick()
        composeRule.onNodeWithTag("chatMessageItem").onChild().assertIsDisplayed()
    }
    
}