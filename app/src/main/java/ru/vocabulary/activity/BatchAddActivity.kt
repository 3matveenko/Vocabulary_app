package ru.vocabulary.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import ru.vocabulary.R
import ru.vocabulary.model.WordListHolder
import ru.vocabulary.model.WordPair

class BatchAddActivity : AppCompatActivity() {

    private lateinit var batchEditText: EditText
    private val cyrillicRegex = Regex("[А-яЁё]")

    private val previewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            batchEditText.text.clear()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batch_add)

        batchEditText = findViewById(R.id.batchEditText)
        val checkButton = findViewById<Button>(R.id.checkButton)

        checkButton.setOnClickListener {
            val text = batchEditText.text.toString()
            val pairs = parseText(text)
            WordListHolder.wordPairs = pairs

            val intent = Intent(this, PreviewActivity::class.java)
            previewLauncher.launch(intent)
        }
    }

    private fun parseText(text: String): List<WordPair> {
        val pairs = mutableListOf<WordPair>()
        val lines = text.split("\n")

        for (line in lines) {
            val parts = line.split(Regex("\\s*[-—]\\s*"))
            if (parts.size == 2) {
                val part1 = parts[0].trim()
                val part2 = parts[1].trim()

                if (part1.isNotBlank() && part2.isNotBlank()) {
                    val part1HasCyrillic = cyrillicRegex.containsMatchIn(part1)
                    val part2HasCyrillic = cyrillicRegex.containsMatchIn(part2)

                    // Swap only if the second part is clearly Russian and the first is not.
                    if (!part1HasCyrillic && part2HasCyrillic) {
                         pairs.add(WordPair(ru = part2, en = part1))
                    } else {
                        // In all other cases (ru/en, ru/ru, en/en), save as is.
                        pairs.add(WordPair(ru = part1, en = part2))
                    }
                }
            }
        }
        return pairs
    }
}