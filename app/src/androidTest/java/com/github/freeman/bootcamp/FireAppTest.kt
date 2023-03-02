package com.github.freeman.bootcamp

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test


class FireAppTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        FireApp::class.java
    )


    @Test
    fun setAndGet() {
        Intents.init()
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            FireApp::class.java
        )
            onView(withId(R.id.phone)).perform(typeText("FireAppTestSetAndGetKey"))
            onView(withId(R.id.email)).perform(typeText("FireAppTestSetAndGetValue"))
            onView(withId(R.id.set)).perform(click())
            onView(withId(R.id.email)).perform(typeText("how to delete this"))
            Thread.sleep(1)
            onView(withId(R.id.get)).perform(click())
            onView(withId(R.id.email)).check(matches(withText("FireAppTestSetAndGetValue")))

        Intents.release()
    }
}