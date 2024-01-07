package com.example.mapYandex.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mapYandex.data.TagDao
import com.example.mapYandex.data.TagDatabase

class MapViewModel (private val tagDao: TagDao) : ViewModel(){
    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return MapViewModel(TagDatabase.getInstance(application).tagDao()) as T

            }
        }
    }
}