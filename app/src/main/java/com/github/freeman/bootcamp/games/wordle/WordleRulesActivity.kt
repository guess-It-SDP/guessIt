package com.github.freeman.bootcamp.games.wordle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextLayoutResult
import com.github.freeman.bootcamp.games.wordle.WordleRulesActivity.Companion.MINIMIZED_MAX_LINES
import com.github.freeman.bootcamp.games.wordle.WordleRulesActivity.Companion.SHOW_LESS
import com.github.freeman.bootcamp.games.wordle.WordleRulesActivity.Companion.SHOW_MORE

class WordleRulesActivity : ComponentActivity() {

    companion object{
        const val WORDLE_RULE_ACTIVITY_TEST_TAG= "wordleRuleActivityTestTag"
        const val SHOW_LESS = "Show Less"
        const val SHOW_MORE = "... Show More"
        const val MINIMIZED_MAX_LINES = 26
    }

    private val rulesText = "The rules of Wordle are elegantly simple.\n" +
            "\n" +
            "Your objective is to guess a secret five-letter word in as few guesses as possible.\n" +
            "\n" +
            "To submit a guess, type any five-letter word and press enter.\n" +
            "\n" +
            "If words only mode is activited, all of your guesses must be real words, according to a dictionary of five-letter words that Wordle allows as guesses. You can’t make up a non-existent word, like AEIOU, just to guess those letters.\n" +
            "\n" +
            "As soon as you’ve submitted your guess, the game will color-code each letter in your guess to tell you how close it was to the letters in the hidden word.\n" +
            "\n" +
            "A Red square means that this letter does not appear in the secret word at all\n" +
            "A yellow square means that this letter appears in the secret word, but it’s in the wrong spot within the word\n" +
            "A green square means that this letter appears in the secret word, and it’s in exactly the right place\n" +
            "Getting a green square or yellow square will get you closer to guessing the real secret word, since it means you’ve guessed a correct letter.\n" +
            "\n" +
            "For example, let’s say you guess “WRITE” and get two green squares on the W and the R, and gray squares for the I, T, and E. Your next guess might be WRONG, WRACK, or WRUNG, since these words start with WR and don’t contain the letters I, T, or E.\n" +
            "\n" +
            "Alternatively, let’s say you guess “WRITE” and get two green squares on the T and the E, and gray squares for the W, R, and I. In that case, your next guess might be BASTE, ELATE, or LATTE, since these words end with TE and don’t contain the letters W, R, or I.\n" +
            "\n" +
            "Remember that the same letter can appear multiple times in the secret word, and there’s no special color coding for letters that appear repeatedly. For example, if the secret word is BELLE and you guess a word with one L and one E, Wordle won’t tell you that both those letters actually appear twice.\n" +
            "\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpandingText(Modifier.testTag(WORDLE_RULE_ACTIVITY_TEST_TAG),text = rulesText)
        }
    }
}

/**
 * expands the text
 * Author https://proandroiddev.com/expandabletext-in-jetpack-compose-b924ea424774
 */
@Composable
fun ExpandingText(modifier: Modifier = Modifier, text: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    var finalText by remember { mutableStateOf(text) }

    val textLayoutResult = textLayoutResultState.value
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalText = "$text $SHOW_LESS"
            }
            !isExpanded && textLayoutResult.hasVisualOverflow -> {
                val lastCharIndex = textLayoutResult.getLineEnd(MINIMIZED_MAX_LINES - 1)
                val showMoreString = SHOW_MORE
                val adjustedText = text
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLast(showMoreString.length)
                    .dropLastWhile { it == ' ' || it == '.' }

                finalText = "$adjustedText$showMoreString"

                isClickable = true
            }
        }
    }

    Text(
        text = finalText,
        maxLines = if (isExpanded) Int.MAX_VALUE else MINIMIZED_MAX_LINES,
        onTextLayout = { textLayoutResultState.value = it },
        modifier = modifier
            .clickable(enabled = isClickable) { isExpanded = !isExpanded }
            .animateContentSize(),
    )
}