package ru.vocabulary.model

import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val wordDao: WordDao) : ViewModelProvider.Factory {
    override fun <T : AndroidXViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModel(wordDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}