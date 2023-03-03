package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.ui.theme.BootcampComposeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingAppTest {
    @get:Rule
    val composeRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity


    @Test
    fun mainScreenIsDisplayed() {
        setMainScreen()

        composeRule.onNode(hasTestTag("mainScreen")).assertIsDisplayed()

    }

    @Test
    fun buttonTextIsCorrect() {
        setMainScreen()

        composeRule.onNode(hasTestTag("greetingButton")).assertTextContains("Greet me!")

    }

    @Test
    fun buttonHasClickAction() {
        setMainScreen()

        composeRule.onNode(hasTestTag("greetingButton")).assertHasClickAction()
    }


    @Test
    fun mainScreenPreviewTest() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainScreenPreview()
            }
        }

        composeRule.onRoot().apply {
            assertIsDisplayed()

            val textNode = composeRule.onNode(hasTestTag("greetingButton"))
            textNode.assertIsDisplayed()

            composeRule.onNode(hasTestTag("greetingInput")).assertIsDisplayed()
        }
    }

    @Test
    fun greetingButtonPreviewTest() {
        composeRule.setContent {
            BootcampComposeTheme {
                GreetingButtonPreview()
            }
        }

        composeRule.onRoot().apply {
            assertIsDisplayed()

            val textNode = composeRule.onNode(hasTestTag("greetingButton"))
            textNode.assertIsDisplayed()

        }

    }

    @Test
    fun greetingInputPreviewTest() {
        composeRule.setContent {
            BootcampComposeTheme {
                GreetingInputPreview()
            }
        }

        composeRule.onRoot().apply {
            assertIsDisplayed()

            composeRule.onNode(hasTestTag("greetingInput")).assertIsDisplayed()


        }

    }


    @Test
    fun intentIsSent() {
        Intents.init()

        setMainScreen()

        composeRule.onNode(hasSetTextAction()).performTextInput("boby")
        composeRule.onNodeWithText("Greet me!").performClick()

        intended(hasComponent(GreetingActivity::class.java.name))

        Intents.release()
    }


    @Test
    fun greetingIsDisplayed() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainScreen()
            }
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("boby")
        composeRule.onNodeWithText("Greet me!").performClick()

        composeRule.onNode(hasTestTag("greeting")).assertIsDisplayed()
    }

    @Test
    fun greetingIsNotDisplayedWhenNameIsEmpty() {
        setMainScreen()

        composeRule.onNodeWithText("Greet me!").performClick()

        composeRule.onNode(hasTestTag("greeting")).assertDoesNotExist()
    }

    @Test
    fun buttonIsDisplayed() {
        setMainScreen()

        composeRule.onNodeWithText("Greet me!").assertIsDisplayed()
    }

    @Test
    fun textFieldLabelIsCorrect() {
        setMainScreen()

        composeRule.onNode(hasTestTag("greetingInput")).assertTextContains("Enter Your Name")
    }




    @Test
    fun textFieldIsDisplayed() {
        setMainScreen()

        composeRule.onNode(hasSetTextAction()).assertIsDisplayed()
    }

    @Test
    fun intentIsReceivedWithRightExtra() {
        // Initialize Intents
        Intents.init()

        // Create the intent with the desired extras
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, GreetingActivity::class.java)
        intent.putExtra("name", "Bob")

        // Launch the activity using the intent
        launchActivity<GreetingActivity>(intent)

        // Perform actions on the UI using Espresso
        //onView(withId(R.id.greetingMessage)).check(matches(withText("Hello Bob!")))
        composeRule.onNodeWithText("Hello, Bob!").assertIsDisplayed()

        // Release Intents
        Intents.release()
    }

    fun setMainScreen() {
        composeRule.setContent {
            BootcampComposeTheme {
                MainScreen()
            }
        }
    }




}