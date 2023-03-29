package com.github.freeman.bootcamp

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule

/**
 * Contains a function to return a node with a test tag.
 */
object Node {
    /**
     * return a function with a test tag
     */
    fun node(testTag: String, composeRule: ComposeContentTestRule): SemanticsNodeInteraction {
       return  composeRule.onNode(hasTestTag(testTag))
    }
}