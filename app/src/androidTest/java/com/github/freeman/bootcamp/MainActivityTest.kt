package com.github.freeman.bootcamp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.core.app.launchActivity
import androidx.test.espresso.matcher.ViewMatchers.*
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()
    private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

//    @Before
//    fun setup() {
//        okHttp3IdlingResource = OkHttp3IdlingResource.create(
//            "okhttp",
//            OkHttpProvider.getOkHttpClient()
//        )
//        IdlingRegistry.getInstance().register(
//            okHttp3IdlingResource
//        )
//
//        mockWebServer.start(8080)
//    }

//    @After
//    fun teardown() {
//        mockWebServer.shutdown()
//        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
//    }

//    @Test
//    fun testSuccessfulResponse() {
//        mockWebServer.dispatcher = object : Dispatcher() {
//            override fun dispatch(request: RecordedRequest): MockResponse {
//                return MockResponse()
//                    .setResponseCode(200)
//                    .setBody(FileReader.readStringFromFile("success_response.json"))
//            }
//        }
//    }

    @Test
    fun clickingIsDisplayed() {
        onView(withId(R.id.fetchDataButton))
            .perform(click())
            .check(matches(isDisplayed()))
    }
}