package com.github.freeman.bootcamp.games.guessit.drawing

import androidx.compose.ui.graphics.Color

// Colors used for the drawing activity, including their shades

private const val NUM_SHADES = 5

val black = Color(0xFF000000)
val red = Color(0xFFB71C1C)
val purple = Color(0xFF4A148C)
val pink = Color(0xFFDA71F1)
val darkBlue = Color(0xFF1A237E)
val blue = Color(0xFF1565C0)
val lightBlue = Color(0xFF03A9F4)
val green = Color(0xFF1B5E20)
val lightGreen = Color(0xFF689F38)
val yellow = Color(0xFFFFEB3B)
val orange = Color(0xFFEF6C00)
val brown = Color(0xFF5C2609)
val grey = Color(0xFF616161)


val blackShades = generateShades(black)
val redShades = generateShades(red)
val purpleShades = generateShades(purple)
val pinkShades = generateShades(pink)
val darkBlueShades = generateShades(darkBlue)
val blueShades = generateShades(blue)
val lightBlueShades = generateShades(lightBlue)
val greenShades = generateShades(green)
val lightGreenShades = generateShades(lightGreen)
val yellowShades = generateShades(yellow)
val orangeShades = generateShades(orange)
val brownShades = generateShades(brown)
val greyShades = generateShades(grey)

val colorArray = listOf(
    brownShades,
    redShades,
    orangeShades,
    yellowShades,
    lightGreenShades,
    greenShades,
    lightBlueShades,
    blueShades,
    darkBlueShades,
    purpleShades,
    pinkShades,
    blackShades,
    greyShades,
)

private fun generateShades(color: Color): List<Color> {
    val shades: ArrayList<Color> = ArrayList()
    for (i in 0 .. 0xFF step 0xFF / NUM_SHADES) {
        val alpha = color.alpha - i.toFloat() / 0xFF
        shades.add(color.copy(alpha = alpha))
    }
    return shades
}