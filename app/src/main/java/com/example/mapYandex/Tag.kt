package com.example.mapYandex

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag {
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val image: Bitmap? = null,
    val name: String,
    val description: String,
    val comment: String
}