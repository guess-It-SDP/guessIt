package com.github.freeman.bootcamp.faceDetection

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class FaceDetectionActivityMoustacheTest {

        /**
         * Factory method to provide android specific implementation of createComposeRule, for a given
         * activity class type A that needs to be launched via an intent.
         * https://stackoverflow.com/questions/68267861/add-intent-extras-in-compose-ui-test
         *
         * @param intentFactory A lambda that provides a Context that can used to create an intent. A intent needs to be returned.
         */
        inline fun <A : ComponentActivity> createAndroidIntentComposeRule(intentFactory: (context: Context) -> Intent): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val intent = intentFactory(context)

            return AndroidComposeTestRule(
                activityRule = ActivityScenarioRule(intent),
                activityProvider = { scenarioRule -> scenarioRule.getActivity() }
            )
        }


        /**
         * Gets the activity from a scenarioRule.
         *
         * https://androidx.tech/artifacts/compose.ui/ui-test-junit4/1.0.0-alpha11-source/androidx/compose/ui/test/junit4/AndroidComposeTestRule.kt.html
         */
        fun <A : ComponentActivity> ActivityScenarioRule<A>.getActivity(): A {
            var activity: A? = null

            scenario.onActivity { activity = it }

            return activity
                ?: throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
        }


    @get:Rule
    val composeRule = createAndroidIntentComposeRule<FaceDetectionActivity> {
        Intent(it, FaceDetectionActivity::class.java).apply {
            putExtra(FaceDetectionActivity.FACE_DETECTION_ACTIVITY_INTENT_NAME, FaceDetectionActivity.Companion.FaceDetectionDrawingType.moustache)
        }
    }


    @Test
    fun BoxContainingPictureExixst() {
        composeRule.onNodeWithTag(FaceDetectionActivity.FACE_DETECTION_TAG).assertExists()
    }

    @Test
    fun companionsIsAccessible() {
        assertEquals(
            FaceDetectionActivity.FACE_DETECTION_TAG, FaceDetectionActivity.FACE_DETECTION_TAG
        )
    }

}