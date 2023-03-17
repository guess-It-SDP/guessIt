package com.github.freeman.bootcamp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class BitmapHandlerTest {

    private val testBitmap = createRandomBitmap(50, 50)

    @Test
    fun bitmapToStringToBitmapGivesOriginal() {
        val stringBitmap = BitmapHandler.bitmapToString(testBitmap)
        val actualBitmap = BitmapHandler.stringToBitmap(stringBitmap)
        assert(testBitmap.sameAs(actualBitmap))
    }

    private fun createRandomBitmap(width: Int, height: Int): Bitmap {
        val random = Random(System.currentTimeMillis())
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        for (x in 0 until width) {
            for (y in 0 until height) {
                paint.color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
                canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
            }
        }
        return bitmap
    }
}