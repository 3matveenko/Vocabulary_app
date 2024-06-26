package ru.vocabulary.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class GetSettings(context: Context) {

    private var sharedPreferences: SharedPreferences
    init {
        sharedPreferences = context.getSharedPreferences("courier", Context.MODE_PRIVATE)
    }

    fun load(key: String): String {
        return sharedPreferences.getString(key, "") ?: return ""
    }

    @SuppressLint("CommitPrefEdits")
    fun save(key: String, string: String) {
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(key, string)
        sharedPreferencesEditor.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun remove(key: String) {
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.remove(key)
        sharedPreferencesEditor.apply()
    }

    fun isNull(key: String): Boolean {
        if (load(key) == "") {
            return true
        }
        return false
    }
}