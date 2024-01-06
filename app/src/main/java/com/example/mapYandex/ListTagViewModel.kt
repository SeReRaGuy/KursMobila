package com.example.mapYandex

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


class ListTagViewModel(private val database: TagDatabase) : ViewModel() {
    var tags: LiveData<List<Tag>> = database.tagDao().findAll()

    fun deleteTag(tagId: Int) {
        thread {
            val tag = tags.value?.first { it.id == tagId }
            tag?.let { viewModelScope.launch { database.tagDao().delete(it) } }
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
                return ListTagViewModel(TagDatabase.getInstance(application)) as T

            }
        }
    }
}