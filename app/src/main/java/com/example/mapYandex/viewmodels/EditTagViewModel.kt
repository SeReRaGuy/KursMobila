package com.example.mapYandex.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mapYandex.Failed
import com.example.mapYandex.Status
import com.example.mapYandex.Success
import com.example.mapYandex.data.Tag
import com.example.mapYandex.data.TagDao
import com.example.mapYandex.data.TagDatabase
import kotlinx.coroutines.launch


class EditTagViewModel(private val tagDao: TagDao, val tagId: Long) :
    ViewModel() {
    private val _dbTag = tagDao.findById(tagId)

    private var _currentTag: Tag? = null

    private val _tag = MediatorLiveData<Tag>()
    val tag: LiveData<Tag> = _tag


    private var _nameError = MutableLiveData<String>()
    val nameError: LiveData<String> = _nameError
    private var _descriptionError = MutableLiveData<String>()
    val descriptionError: LiveData<String> = _descriptionError
    private var _commentError = MutableLiveData<String>()
    val commentError: LiveData<String> = _commentError

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> = _status

    private var _image = MutableLiveData<Bitmap?>()
    val image: LiveData<Bitmap?> = _image

    init {
        _tag.addSource(_dbTag) { //-
            if (!checkIfNewTag()) _tag.value = it
            else _tag.value = getEmptyTag()
        }
    }

    fun setImage(image: Bitmap?) {
        _image.value = image
    }

    fun validateName(name: String) {
        if (name.isBlank() && (_currentTag?.name ?: "").isNotBlank()) {
            _nameError.value = "Error"
        }
        if (name != tag.value?.name) {
            _currentTag = tag.value?.copy(name = name)
        }
    }

    fun validateDescription(description: String) {
        if (description.isBlank() && (_currentTag?.description ?: "").isNotBlank()) {
            _descriptionError.value = "Error"
        }
        if (description != tag.value?.description) {
            _currentTag = tag.value?.copy(description = description)
        }
    }

    fun validateComment(comment: String) {
        if (comment.isBlank() && (_currentTag?.comment ?: "").isNotBlank()) {
            _commentError.value = "Error"
        }
        if (comment != tag.value?.comment) {
            _currentTag = tag.value?.copy(comment = comment)
        }
    }

    fun saveTag(
        name: String, description: String, comment: String
    ) {
        val image = image.value
        if (checkAllIfNotBlank(name, description, comment)) {
            _status.value = Failed("One or several fields are blank")
        } else {
            val newTag = tag.value?.copy(
                name = name,
                description = description,
                comment = comment,
                image = image
            )
            newTag?.let {
                viewModelScope.launch {
                    if (checkIfNewTag()) {
                        tagDao.insert(it)
                    } else {
                        tagDao.update(it)
                    }
                    _status.value = Success()
                }
            }
        }
    }

    private fun getEmptyTag() = Tag(null, null, null, null,null,null,null)

    fun checkIfNewTag() = tagId == -1L


    private fun checkAllIfNotBlank(
        name: String,
        description: String,
        comment: String,
    ) = name.isBlank() || description.isBlank() || comment.isBlank()

    override fun onCleared() {
        _tag.removeSource(_dbTag)
        super.onCleared()
    }

    companion object {
        fun Factory(tagId: Long): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return EditTagViewModel(TagDatabase.getInstance(application).tagDao(), tagId) as T
            }
        }
    }
}