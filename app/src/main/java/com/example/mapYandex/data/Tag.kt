package com.example.mapYandex.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag (
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val name: String?,
    val description: String?,
    val comment: String?,
    val image: Bitmap? = null,
    val cord1: Double?,
    val cord2: Double?
//    val place: PlacemarkMapObject? = null
)