package com.example.mapYandex.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mapYandex.data.Tag
import com.example.mapYandex.data.TagDao
import com.example.mapYandex.data.TagDatabase
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


class ListTagViewModel(private val tagDao: TagDao) : ViewModel() {
    var tags: LiveData<List<Tag>> = tagDao.findAll()

    fun deleteTag(tagId: Long) {
        thread {
            val tag = tags.value?.first { it.id == tagId }
            tag?.let { viewModelScope.launch { tagDao.delete(it) } }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ListTagViewModel(TagDatabase.getInstance(application).tagDao()) as T

            }
        }
    }
}