package ru.vocabulary.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BatchAddViewModel : ViewModel() {

    private val _wordPairs = MutableStateFlow<List<WordPair>>(emptyList())
    val wordPairs = _wordPairs.asStateFlow()

    fun setWordPairs(pairs: List<WordPair>) {
        _wordPairs.value = pairs
    }
}