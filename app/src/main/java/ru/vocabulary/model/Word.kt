package ru.vocabulary.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "words", indices = [Index(value = ["ru"], unique = true)])
data class Word (

    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    var ru:String,
    var en:String,
    val date:Long
)
