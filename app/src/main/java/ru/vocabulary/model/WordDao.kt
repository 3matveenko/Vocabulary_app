package ru.vocabulary.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordDao {

    @Insert
    fun insert(word: Word)

    @Update
    fun update(word: Word)

    @Query("SELECT COUNT(*) FROM words")
    fun count():Int

    @Query("SELECT * FROM words WHERE ru != :wordRu ORDER BY RANDOM() LIMIT 1")
    fun getRandomWord(wordRu: String): Word?

}