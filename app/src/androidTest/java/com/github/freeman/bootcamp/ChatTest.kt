package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun initDataBase(): DatabaseReference {
        val chatId = "TestChatId01"
        val db = Firebase.database
        db.useEmulator("10.0.2.2", 9000)
        return Firebase.database.getReference("Chat/$chatId")
    }

    @Test
    fun activateChatButtonIsDisplayed() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("activateChatButton").assertIsDisplayed()

    }

    @Test
    fun chatScreenIsNotDisplayedBeforeActivation() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("chatScreen").assertDoesNotExist()

    }

    @Test
    fun chatScreenIsDisplayedAfterActivation() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("activateChatButton").performClick()
        composeRule.onNodeWithTag("chatScreen").assertIsDisplayed()

    }

    @Test
    fun bottomBarIsDisplayedAfterActivation() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("activateChatButton").performClick()
        composeRule.onNodeWithTag("bottomBar").assertIsDisplayed()

    }

    @Test
    fun backGroundComposableIsDisplayed() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("backGroundComposable").assertIsDisplayed()
        composeRule.onNodeWithTag("activateChatButton").performClick()
        composeRule.onNodeWithTag("backGroundComposable").assertIsDisplayed()

    }

    @Test
    fun backButtonDeactivatesChat() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("activateChatButton").performClick()
        composeRule.onNodeWithTag("chatScreen").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithTag("chatScreen").assertDoesNotExist()

    }

    @Test
    fun mainPreviewDisplaysMain() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainPreview()
            }
        }

        composeRule.onNodeWithTag("activateChatButton").assertIsDisplayed()
        composeRule.onNodeWithTag("chatScreen").assertDoesNotExist()
        composeRule.onNodeWithTag("backGroundComposable").assertIsDisplayed()

    }

    @Test
    fun sendingMessageDisplaysInChat() {
        val dbref = initDataBase()


        composeRule.setContent {
            BootcampComposeTheme {
                Main(dbref)
            }
        }

        composeRule.onNodeWithTag("activateChatButton").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Bonjour Monde !")
        composeRule.onNodeWithTag("sendButton").performClick()
        composeRule.onNodeWithTag("chatMessageItem").onChild().assertTextContains("me: Bonjour Monde !")

    }

}