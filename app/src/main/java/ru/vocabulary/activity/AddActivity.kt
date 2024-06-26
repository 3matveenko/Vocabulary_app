package ru.vocabulary.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.vocabulary.MainActivity.Companion.staticword
import ru.vocabulary.R
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.Word


class AddActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val editRu = findViewById<EditText>(R.id.editRu)
        val editEn = findViewById<EditText>(R.id.editEn)

        val update = intent.getStringExtra("update")

        if (update != null) {
            editRu.setText(staticword.ru)
            editEn.setText(staticword.en)
        }

        val wordDao = AppDatabase.getInstance(applicationContext).wordDao()
        var viewModel = ViewModel(wordDao)

        findViewById<Button>(R.id.add).setOnClickListener {
            val wordRu = findViewById<EditText>(R.id.editRu).text.toString()
            val wordEn = findViewById<EditText>(R.id.editEn).text.toString()
            if (update != null) {
                staticword.ru = wordRu
                staticword.en = wordEn
                viewModel.update(staticword)
                Toast.makeText(this, "Слово обновлено", Toast.LENGTH_LONG).show()
                finish()
            } else {
                if (wordRu != "" && wordEn != "") {
                    viewModel.insertWord(
                        Word(
                            ru = wordRu,
                            en = wordEn,
                            date = System.currentTimeMillis()
                        )
                    )
                    Toast.makeText(this, "Слово успешно добавлено", Toast.LENGTH_LONG).show()
                    findViewById<EditText>(R.id.editEn).setText("")
                    findViewById<EditText>(R.id.editRu).setText("")
                } else {
                    Toast.makeText(this, "Поля должны быть заполнены!", Toast.LENGTH_LONG).show()
                }
            }


        }
        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }
    }
}