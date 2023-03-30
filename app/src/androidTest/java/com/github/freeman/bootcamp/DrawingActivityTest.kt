package com.github.freeman.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingActivityTest {

    private val blankBitmapString =
        "iVBORw0KGgoAAAANSUhEUgAABDgAAAW7CAYAAADSQwFNAAAAAXNSR0IArs4c6QAAAARzQklUCAgICHwIZIgAACAASURBVHic7NhBDQAgEMAwwL/nQwQPsqRVsPf2zMwCAAAACDu/AwAAAABeGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEBAAAA5BkcAAAAQJ7BAQAAAOQZHAAAAECewQEAAADkGRwAAABAnsEB3HbsgAQAAABA0P/X7Qh0hgAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAEJvzWwAABS5JREFUAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYE9wAAAAAHuCAwAAANgTHAAAAMCe4AAAAAD2BAcAAACwJzgAAACAPcEBAAAA7AkOAAAAYC96Mg9yYL04IgAAAABJRU5ErkJggg=="
    private val blankBitmap = BitmapHandler.stringToBitmap(blankBitmapString)
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent() {
        composeRule.setContent {
            DrawingScreen()
        }
    }

    private fun setContentWith(dbref: DatabaseReference, gameId: String) {
        composeRule.setContent {
            DrawingScreen(dbref, gameId)
        }
    }

    @Test
    fun drawingScreenIsDisplayed() {
        setContent()
        composeRule.onNodeWithTag(context.getString(R.string.drawing_screen)).assertIsDisplayed()
    }

    @Test
    fun colorButtonHasClickAction() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_color))
            .assertHasClickAction()
    }

    @Test
    fun widthButtonHasClickAction() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
            .assertHasClickAction()
    }

    @Test
    fun undoButtonHasClickAction() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.undo))
            .assertHasClickAction()
    }

    @Test
    fun redoButtonHasClickAction() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.redo))
            .assertHasClickAction()
    }

    @Test
    fun doneButtonHasClickAction() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.drawing_done))
            .assertHasClickAction()
    }

    @Test
    fun widthSliderAppearsOnWidthButtonClick() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
            .performClick()
        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
    }

    @Test
    fun sliderStaysOnScreenWhenClickingUndo() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
            .performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.undo)).performClick()
        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
    }

    @Test
    fun sliderStaysOnScreenWhenClickingRedo() {
        setContent()
        composeRule.onNodeWithContentDescription(context.getString(R.string.stroke_width))
            .performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.redo)).performClick()
        composeRule.onNodeWithTag(context.getString(R.string.width_slider)).assertIsDisplayed()
    }

    @Test
    fun drawingScreenContainsTimer() {
        setContent()
        composeRule.onNode(hasTestTag("timerScreen")).assertIsDisplayed()
    }

    @Test
    fun doneButtonSendsRightBitmapToDB() {
        val testId = "testId"
        FirebaseEmulator.init()
        val dbref = FirebaseSingletons.database.get().database.getReference("Images")
        setContentWith(dbref, testId)
        composeRule.onNodeWithContentDescription(context.getString(R.string.drawing_done))
            .performClick()
        dbref.child(testId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val bitmapOnDB = BitmapHandler.stringToBitmap(snapshot.getValue<String>()!!)
                    assertThat(bitmapOnDB, not(nullValue()))
                    assert(bitmapOnDB!!.sameAs(blankBitmap))
                    dbref.child(testId).removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}