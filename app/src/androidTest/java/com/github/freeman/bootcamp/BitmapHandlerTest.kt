package com.github.freeman.bootcamp

import android.graphics.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.freeman.bootcamp.utilities.BitmapHandler
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class BitmapHandlerTest {
    @Test
    fun bitmapToStringToBitmapGivesOriginal() {
        val testBitmap = createRandomBitmap(50, 50)
        val byteArrayOutputStream = ByteArrayOutputStream()
        testBitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val expectedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val stringBitmap = BitmapHandler.bitmapToString(expectedBitmap)
        val actualBitmap = BitmapHandler.stringToBitmap(stringBitmap)
        assert(expectedBitmap.sameAs(actualBitmap))
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