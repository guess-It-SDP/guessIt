package com.github.freeman.bootcamp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MapsActivityTest {

   @Test
   fun mapsDisplayedTest() {
      val i = Intent(ApplicationProvider.getApplicationContext(), MapsActivity::class.java)
      val activityScenario: ActivityScenario<AppCompatActivity> = ActivityScenario.launch(i)
      activityScenario.use {
         onView(ViewMatchers.withId(R.id.map)).check(matches(isDisplayed()))
      }
   }
}