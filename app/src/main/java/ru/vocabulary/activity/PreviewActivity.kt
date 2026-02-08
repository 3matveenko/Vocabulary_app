package ru.vocabulary.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.vocabulary.R
import ru.vocabulary.adapter.WordPairAdapter
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.ViewModelFactory
import ru.vocabulary.model.Word
import ru.vocabulary.model.WordListHolder

class PreviewActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels { ViewModelFactory(AppDatabase.getInstance(applicationContext).wordDao()) }
    private lateinit var wordPairAdapter: WordPairAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val recyclerView = findViewById<RecyclerView>(R.id.previewRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))

        WordListHolder.wordPairs?.let {
            wordPairAdapter = WordPairAdapter(it.toMutableList())
            recyclerView.adapter = wordPairAdapter
        }

        findViewById<Button>(R.id.saveAllButton).setOnClickListener {
            if(::wordPairAdapter.isInitialized) {
                val updatedPairs = wordPairAdapter.getUpdatedWordPairs()
                val wordsToSave = updatedPairs.map { Word(ru = it.ru, en = it.en, date = System.currentTimeMillis()) }
                viewModel.insertAll(wordsToSave)

                Toast.makeText(this, "Сохранено ${wordsToSave.size} слов!", Toast.LENGTH_SHORT).show()

                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Нет слов для сохранения", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear the holder to avoid memory leaks
        WordListHolder.wordPairs = null
    }
}