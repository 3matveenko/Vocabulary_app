package ru.vocabulary.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vocabulary.MainActivity.Companion.staticword

class ViewModel(private val wordDao: WordDao) : ViewModel() {

    fun insertWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            wordDao.insert(word)
        }
    }

//    fun count(){
//        viewModelScope.launch (Dispatchers.IO)  {
//            wordDao.count()
//        }
//    }

    fun getRandome(){
        viewModelScope.launch(Dispatchers.IO) {
            staticword = wordDao.getRandomWord()!!
        }
    }
}