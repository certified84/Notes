package com.certified.notes.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.lang.Exception

class Converters {

    @TypeConverter
    fun toByteArray(bitmap: Bitmap?): ByteArray? {
        return if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        } else
            null
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else
            null
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return try {
            uri?.toString()
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun stringToUri(string: String?): Uri? {
        return if (string != null) {
            try {
                Uri.parse(string)
            } catch (e: Exception) {
                null
            }
        } else null
    }

//    TODO: uriToBitmap()

//    TODO: bitmapToUri()
}