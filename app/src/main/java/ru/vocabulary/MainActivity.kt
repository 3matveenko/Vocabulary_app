package ru.vocabulary

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.vocabulary.activity.AddActivity
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.GetSettings
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.ViewModelFactory
import ru.vocabulary.model.Word
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels { ViewModelFactory(AppDatabase.getInstance(applicationContext).wordDao()) }

    private var switchToggleValue: String = "ru"
    private var currentWord: Word? = null

    @SuppressLint("MissingInflatedId", "CutPasteId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wordMainTextView = findViewById<TextView>(R.id.word_main)
        val wordHiddenTextView = findViewById<TextView>(R.id.word_hidden)
        val wordCounterTextView = findViewById<TextView>(R.id.word_count)
        val showTranslateButton = findViewById<Button>(R.id.show_translete)
        val nextWordButton = findViewById<Button>(R.id.next_word)

        lifecycleScope.launch {
            viewModel.wordCount.collect { count ->
                wordCounterTextView.text = "Осталось слов: $count"
            }
        }

        lifecycleScope.launch {
            viewModel.currentWord.collect { word ->
                currentWord = word
                if (word != null) {
                    setMainTextView(word)
                    wordHiddenTextView.text = "***"
                    showTranslateButton.visibility = View.VISIBLE
                    nextWordButton.text = "Следующее"
                } else {
                    wordMainTextView.text = "***"
                    wordHiddenTextView.text = "***"
                    showTranslateButton.visibility = View.GONE
                    nextWordButton.text = "Старт!"
                }
            }
        }

        val sButton = findViewById<Button>(R.id.s)
        val poButton = findViewById<Button>(R.id.po)
        val settings = GetSettings(applicationContext)
        val dateFormat = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())

        // --- Bulletproof self-healing date logic ---
        fun getValidDateString(key: String): String {
            val loadedString = settings.load(key)
            var isInvalid = false

            if (loadedString.isNullOrBlank()) {
                isInvalid = true
            } else {
                try {
                    dateFormat.parse(loadedString)
                } catch (e: ParseException) {
                    isInvalid = true
                }
            }

            if (isInvalid) {
                val todayString = dateFormat.format(Date())
                settings.save(key, todayString)
                return todayString
            } else {
                return loadedString!!
            }
        }

        sButton.text = getValidDateString("s")
        poButton.text = getValidDateString("po")

        val switch = findViewById<Switch>(R.id.change_language)
        switch.text = switchToggleValue

        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.settings_alert, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.but_delete).setOnClickListener {
                currentWord?.let {
                    viewModel.delete(it)
                    Toast.makeText(this,"Слово удалено!",Toast.LENGTH_SHORT).show()
                    getNextWord() // We call getNextWord to refresh the word after deletion
                    dialog.dismiss()
                } ?: Toast.makeText(this,"Слово не выбрано!",Toast.LENGTH_SHORT).show()

            }
            dialogView.findViewById<Button>(R.id.but_update).setOnClickListener {
                currentWord?.let {
                    val intent = Intent(this, AddActivity::class.java)
                    intent.putExtra("EXTRA_WORD_ID", it.id)
                    startActivity(intent)
                } ?: Toast.makeText(this,"Слово не выбрано!",Toast.LENGTH_SHORT).show()
            }

            dialogView.findViewById<ImageView>(R.id.exit_alert).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        showTranslateButton.setOnClickListener{
            currentWord?.let {
                if(switchToggleValue=="en"){
                    wordHiddenTextView.text = it.ru
                } else {
                    wordHiddenTextView.text = it.en
                }
            }
        }

        nextWordButton.setOnClickListener {
            getNextWord()
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            switchToggleValue = if (isChecked) "en" else "ru"
            switch.text = switchToggleValue
            currentWord?.let { 
                setMainTextView(it)
                setHiddenTextView(it)
            }
        }

        findViewById<Button>(R.id.goToAdd).setOnClickListener{
            startActivity(Intent(this@MainActivity,AddActivity::class.java))
        }

        sButton.setOnClickListener {
            showDatePickerDialogS()
        }

        poButton.setOnClickListener {
            showDatePickerDialogPo()
        }
    }

    private fun showDatePickerDialogS() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateS = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                findViewById<Button>(R.id.s).text = selectedDateS
                GetSettings(applicationContext).save("s",selectedDateS)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showDatePickerDialogPo() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDatePo = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                findViewById<Button>(R.id.po).text = selectedDatePo
                GetSettings(applicationContext).save("po",selectedDatePo)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun setMainTextView(word: Word){
        val wordMainTextView = findViewById<TextView>(R.id.word_main)
        if(switchToggleValue=="en"){
            wordMainTextView.text = word.en
        } else {
            wordMainTextView.text = word.ru
        }
    }

    private fun setHiddenTextView(word: Word){
        val textView = findViewById<TextView>(R.id.word_hidden)
        if(textView.text!="***"){
            if(switchToggleValue=="en"){
                textView.text = word.ru
            } else {
                textView.text = word.en
            }
        }
    }

    private fun getNextWord(){
        val dateFormat = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())
        val sButton = findViewById<Button>(R.id.s)
        val poButton = findViewById<Button>(R.id.po)
        try {
            // Since onCreate now guarantees the dates are valid, we can parse them safely.
            val dates = dateFormat.parse(sButton.text.toString())
            val dateE = dateFormat.parse(poButton.text.toString())

            // A null check is still best practice, even with the safeguard in onCreate.
            if (dates != null && dateE != null) {
                val dateStart = dates.time
                val dateEnd = dateE.time
                viewModel.getRandome(dateStart, dateEnd)
            } else {
                Toast.makeText(this, "Ошибка: не удалось прочитать даты.", Toast.LENGTH_LONG).show()
            }
        } catch (e: ParseException) {
            // This is now a secondary safeguard and should not be triggered.
            Toast.makeText(this, "Произошла ошибка с датами. Попробуйте выбрать их заново.", Toast.LENGTH_LONG).show()
        }
    }
}