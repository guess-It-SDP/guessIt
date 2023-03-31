package com.github.freeman.bootcamp.videocall

/**
 * TextFieldState containing a text string and possibly error string
 */
data class TextFieldState(
    val text: String = "",
    val error : String? = null
)
