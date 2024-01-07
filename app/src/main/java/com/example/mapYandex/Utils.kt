package com.example.mapYandex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher

fun Uri?.bitmap(context: Context): Bitmap? {
    return this?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        }
    }
}

sealed class Status(var isProcessed: Boolean = false)
class Success : Status()
class Failed(val message: String) : Status()

open class CustomEmptyTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable?) = Unit
}

interface ActionInterface {
    fun onItemClick(tagId: Long)
    fun onDeleteTag(tagId: Long)
}