package com.github.freeman.bootcamp.faceDetection

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.content.ContextCompat.startActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import android.app.Activity

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaceDetectionActivityHairTest {


    /**
     * https://stackoverflow.com/questions/68267861/add-intent-extras-in-compose-ui-test
     * Uses a [ComposeTestRule] created via [createEmptyComposeRule] that allows setup before the activity
     * is launched via [onBefore]. Assertions on the view can be made in [onAfterLaunched].
     */
    inline fun <reified A: Activity> ComposeTestRule.launch(
        onBefore: () -> Unit = {},
        intentFactory: (Context) -> Intent = { Intent(ApplicationProvider.getApplicationContext(), A::class.java) },
        onAfterLaunched: ComposeTestRule.() -> Unit
    ) {
        onBefore()

        val context = ApplicationProvider.getApplicationContext<Context>()
        ActivityScenario.launch<A>(intentFactory(context))

        onAfterLaunched()
    }
    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun firstTimeLogIn() = composeRule.launch<FaceDetectionActivity>(
        onBefore = {
            // Setup things before the intent
        },
        intentFactory = {
            Intent(it, FaceDetectionActivity::class.java).apply {
                putExtra(FaceDetectionActivity.FACE_DETECTION_ACTIVITY_INTENT_NAME, FaceDetectionActivity.Companion.FaceDetectionDrawingType.hair.toString())
            }
        },
        onAfterLaunched = {
          onNodeWithTag(FaceDetectionActivity.FACE_DETECTION_TAG).assertExists()
        })

    @Test
    fun companionsIsAccessible() {
        assertEquals(
            FaceDetectionActivity.FACE_DETECTION_TAG, FaceDetectionActivity.FACE_DETECTION_TAG
        )
    }

}