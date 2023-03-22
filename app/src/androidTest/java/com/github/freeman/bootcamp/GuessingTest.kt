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

//@RunWith(AndroidJUnit4::class)
//class GuessingTest {
//    @get:Rule
//    val composeRule = createComposeRule()
//
//    @Test
//    fun guessingScreenIsDisplayed() {
//        initScreenWithDatabase()
//        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
//    }
//
//    @Test
//    fun guessingScreenContainsCorrectText() {
//        initScreenWithDatabase()
//        composeRule.onNodeWithTag("guessText").assertTextContains("Your turn to guess!")
//    }
//
//    @Test
//    fun guessesListIsDisplayed() {
//        initScreenWithDatabase()
//        composeRule.onNodeWithTag("guessesList").assertIsDisplayed()
//    }
//
//    @Test
//    fun guessingBarIsDisplayed() {
//        initScreenWithDatabase()
//        composeRule.onNodeWithTag("guessingBar").assertIsDisplayed()
//    }
//
//    @Test
//    fun guessingPreviewDisplaysGuessingScreen() {
//        initScreenWithoutDatabase()
//        composeRule.onNodeWithTag("guessingScreen").assertIsDisplayed()
//        composeRule.onNodeWithTag("guessText").assertTextContains("Your turn to guess!")
//        composeRule.onNodeWithTag("guessesList").assertIsDisplayed()
//        composeRule.onNodeWithTag("guessingBar").assertIsDisplayed()
//    }
//
//    @Test
//    fun guessIsDisplayedInGuessingList() {
//        initScreenWithDatabase()
//        composeRule.onNode(hasSetTextAction()).performTextInput("House")
//        composeRule.onNodeWithTag("guessButton").performClick()
//        composeRule.onNodeWithTag("guessItem").onChild().assertIsDisplayed()
//    }
//
//
//    private fun initGuessingDataBase(): DatabaseReference {
//        val db = Firebase.database
//        val guessGameId = "GameTestGuessesId"
//        db.useEmulator("10.0.2.2", 9000)
//        return Firebase.database.getReference("Guesses/$guessGameId")
//    }
//
//    private fun initScreenWithoutDatabase() {
//        composeRule.setContent {
//            BootcampComposeTheme {
//                GuessingPreview()
//            }
//        }
//    }
//
//    private fun initScreenWithDatabase() {
//        val database = initGuessingDataBase()
//
//        composeRule.setContent {
//            BootcampComposeTheme {
//                GuessingScreen(database)
//            }
//        }
//    }
//
//
//
//}