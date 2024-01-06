package com.example.mapYandex

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag (
    @PrimaryKey val id: Int,
    val name: String?,
    val description: String?,
    val comment: String?,
    val image: Bitmap? = null
)