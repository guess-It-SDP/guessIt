package com.github.freeman.bootcamp.wordle

import android.content.Context
import android.content.Intent

class IntentFactory { // I'm trying to use mockk for depencies testing but it doesn't work #mockk
    fun <T>create(context: Context, activityClass: Class<T>): Intent {
        return Intent(context, activityClass)
    }
}