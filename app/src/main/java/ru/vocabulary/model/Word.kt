package ru.vocabulary.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word (

    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    var ru:String,
    var en:String,
    val date:Long
)



