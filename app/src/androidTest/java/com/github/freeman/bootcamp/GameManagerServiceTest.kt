package com.github.freeman.bootcamp

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.games.guessit.GameManagerService
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameManagerServiceTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun serviceIsLaunched() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, GameManagerService::class.java)
        intent.putExtra(context.getString(R.string.gameId_extra),
            context.getString(R.string.test_game_id))
        context.startService(intent)
    }
}