package com.example.mapYandex

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class TagConverters {
    @TypeConverter
    fun fromBitmapToByteArray(value: Bitmap?): ByteArray? {
        val stream = ByteArrayOutputStream()
        value?.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun fromByteArrayToBitmap(value: ByteArray?): Bitmap? {
        return value?.let { BitmapFactory.decodeByteArray(value, 0, it.size) }
    }
}