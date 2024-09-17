
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.freeman.bootcamp.facedetection.FaceDetectionActivity
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement


@LargeTest
@RunWith(AndroidJUnit4::class)
class LateActivityLaunchTest {

    private lateinit var activityScenario: ActivityScenario<FaceDetectionActivity>

    @get:Rule
    val rule = AndroidComposeTestRule(EmptyTestRule()) {
        var activity: FaceDetectionActivity? = null
        activityScenario.onActivity { activity = it }
        checkNotNull(activity) { "Activity didn't launch" }
    }

    class EmptyTestRule : TestRule {
        override fun apply(base: Statement, description: Description) = base
    }

    @Test
    fun test() {
        setupSomethingFirst()

        ActivityScenario.launch<FaceDetectionActivity>(
            Intent(ApplicationProvider.getApplicationContext(), FaceDetectionActivity::class.java)
        ).use {
                rule.onNodeWithTag(FaceDetectionActivity.FACE_DETECTION_TAG).assertExists()
                assertEquals(
                    FaceDetectionActivity.FACE_DETECTION_TAG, FaceDetectionActivity.FACE_DETECTION_TAG
                )
        }
    }

    private fun setupSomethingFirst() {
    }
}

class CustomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        }
    }
}