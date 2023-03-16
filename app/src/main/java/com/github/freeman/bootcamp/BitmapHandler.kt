package com.github.freeman.bootcamp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.util.Base64
import java.io.ByteArrayOutputStream

class BitmapHandler(private val gameId: String) {

    private val dbRef = Firebase.database.getReference("Images/$gameId")

    fun uploadBitmap(bitmap: Bitmap) {
        dbRef.setValue(bitmapToString(bitmap))
    }

    fun fetchBitmap(gameId: String): Bitmap? {
        return stringToBitmap(dbRef.get().toString())
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun stringToBitmap(encodedString: String): Bitmap? {
        val decodedString = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}