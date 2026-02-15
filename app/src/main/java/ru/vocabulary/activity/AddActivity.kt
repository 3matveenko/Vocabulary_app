package ru.vocabulary.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vocabulary.R
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.ViewModelFactory
import ru.vocabulary.model.Word

class AddActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels { ViewModelFactory(AppDatabase.getInstance(applicationContext).wordDao()) }
    private var wordToUpdate: Word? = null

    // Regex to check for any Cyrillic characters
    private val cyrillicRegex = Regex("[А-яЁё]")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val editRu = findViewById<EditText>(R.id.editRu)
        val editEn = findViewById<EditText>(R.id.editEn)
        val addButton = findViewById<Button>(R.id.add)

        val wordId = intent.getLongExtra("EXTRA_WORD_ID", -1L)
        val isUpdateMode = wordId != -1L

        if (isUpdateMode) {
            addButton.text = "Обновить"
            lifecycleScope.launch(Dispatchers.IO) {
                val word = AppDatabase.getInstance(applicationContext).wordDao().getWordById(wordId)
                withContext(Dispatchers.Main) {
                    wordToUpdate = word
                    wordToUpdate?.let {
                        editRu.setText(it.ru)
                        editEn.setText(it.en)
                    }
                }
            }
        }

        addButton.setOnClickListener {
            val input1 = editRu.text.toString().trim()
            val input2 = editEn.text.toString().trim()

            if (input1.isBlank() || input2.isBlank()) {
                Toast.makeText(this, "Поля должны быть заполнены!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val ruWord: String
            val enWord: String

            val input1HasCyrillic = cyrillicRegex.containsMatchIn(input1)
            val input2HasCyrillic = cyrillicRegex.containsMatchIn(input2)

            // Swap only if the second input is clearly Russian and the first is not.
            if (!input1HasCyrillic && input2HasCyrillic) {
                ruWord = input2
                enWord = input1
            } else {
                // In all other cases (ru/en, ru/ru, en/en), save as is.
                ruWord = input1
                enWord = input2
            }

            if (isUpdateMode) {
                wordToUpdate?.let {
                    val updatedWord = it.copy(ru = ruWord, en = enWord)
                    viewModel.update(updatedWord)
                    Toast.makeText(this, "Слово обновлено", Toast.LENGTH_LONG).show()
                    finish()
                }
            } else {
                val newWord = Word(ru = ruWord, en = enWord, date = System.currentTimeMillis())
                viewModel.insertWord(newWord)
                Toast.makeText(this, "Слово успешно добавлено", Toast.LENGTH_LONG).show()
                editEn.text.clear()
                editRu.text.clear()
            }
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }
    }
}