package ru.vocabulary.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WordPair(val ru: String, val en: String) : Parcelable
