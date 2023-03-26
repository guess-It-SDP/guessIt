package com.github.freeman.bootcamp.wordle

import android.content.Context
import android.content.Intent

class IntentFactory {
    fun <T>create(context: Context, activityClass: Class<T>): Intent {
        return Intent(context, activityClass)
    }
}