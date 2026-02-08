package ru.vocabulary.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel(private val wordDao: WordDao) : ViewModel() {

    private val wordsForSession = mutableListOf<Word>()

    private val _wordCount = MutableStateFlow(0)
    val wordCount: StateFlow<Int> = _wordCount.asStateFlow()

    private val _currentWord = MutableStateFlow<Word?>(null)
    val currentWord: StateFlow<Word?> = _currentWord.asStateFlow()

    fun insertWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            wordDao.insert(word)
        }
    }

    fun insertAll(words: List<Word>) {
        viewModelScope.launch(Dispatchers.IO) {
            wordDao.insertAll(words)
        }
    }

    fun getRandome(dateStart:Long,dateEnd:Long){
        viewModelScope.launch(Dispatchers.IO) {
            if (wordsForSession.isEmpty()) {
                val wordsFromDb = wordDao.getWordsBetweenDates(dateStart, dateEnd)
                wordsForSession.addAll(wordsFromDb)
                _wordCount.value = wordsForSession.size
            }

            if (wordsForSession.isNotEmpty()) {
                var randomWord = wordsForSession.random()
                val currentStaticWord = _currentWord.value

                if (wordsForSession.size > 1 && currentStaticWord != null && randomWord.ru == currentStaticWord.ru) {
                    val shuffledList = wordsForSession.shuffled()
                    randomWord = shuffledList.firstOrNull { it.ru != currentStaticWord.ru } ?: randomWord
                }

                _currentWord.value = randomWord
                wordsForSession.remove(randomWord)
                _wordCount.value = wordsForSession.size
            } else {
                _currentWord.value = null // No more words
            }
        }
    }
    fun update(word: Word){
        viewModelScope.launch(Dispatchers.IO){
            try {
                wordDao.update(word)
            } catch (e: Exception){

            }
        }
    }
    fun delete(word: Word){
        viewModelScope.launch(Dispatchers.IO){
            try {
                wordDao.delete(word)
            } catch ( e:Exception){

            }
        }
    }
}