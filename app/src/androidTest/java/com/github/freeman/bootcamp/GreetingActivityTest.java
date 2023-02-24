package com.github.freeman.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;



import android.app.Activity;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {
    @Test
    public void mySecondTest() {
        Intents.init();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
        String name = "Bob";
        intent.putExtra("name", name); //Optional parameters
        try(ActivityScenario<Activity> as = ActivityScenario.launch(intent)) {
            onView(ViewMatchers.withId(R.id.greetingMessage)).check(matches(ViewMatchers.withText("Hello Bob!")));
        }

        Intents.release();
    }
}
