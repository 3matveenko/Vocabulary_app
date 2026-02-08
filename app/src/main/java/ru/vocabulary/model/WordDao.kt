package ru.vocabulary.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordDao {

    @Insert
    fun insert(word: Word)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(words: List<Word>)

    @Update
    fun update(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordById(id: Int): Word?

    @Query("SELECT COUNT(*) FROM words")
    fun count():Int

    @Query("SELECT * FROM words WHERE date BETWEEN :startDate AND :endDate")
    fun getWordsBetweenDates(startDate: Long, endDate: Long): List<Word>

    @Query("SELECT * FROM words WHERE ru != :wordRu AND date BETWEEN :startDate AND :endDate ORDER BY RANDOM() LIMIT 1")
    fun getRandomWordBetweenDates(wordRu: String, startDate: Long, endDate: Long): Word?

    @Query("SELECT * FROM words WHERE ru != :wordRu ORDER BY RANDOM() LIMIT 1")
    fun getRandomWord(wordRu: String): Word?

}