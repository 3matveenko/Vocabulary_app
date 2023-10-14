package ru.vocabulary.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vocabulary.MainActivity.Companion.staticCount
import ru.vocabulary.MainActivity.Companion.staticword

class ViewModel(private val wordDao: WordDao) : ViewModel() {

    fun insertWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            wordDao.insert(word)
        }
    }

    fun count(){
        viewModelScope.launch (Dispatchers.IO)  {
            staticCount = wordDao.count().toString()
        }
    }

    fun getRandome(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                staticword = wordDao.getRandomWord(staticword.ru)!!
            } catch (e:Exception){

            }

        }
    }
}