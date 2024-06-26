package ru.vocabulary

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import ru.vocabulary.activity.AddActivity
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.GetSettings
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.Word
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object{
         var starter:String = ""
         var staticword :Word = Word(0,"***","***",0)
         var switchToggleValue:String = "ru"
         var staticCount:String = "0"
    }
    @SuppressLint("MissingInflatedId", "CutPasteId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wordDao = AppDatabase.getInstance(applicationContext).wordDao()
        var viewModel = ViewModel(wordDao)

        setContentView(R.layout.activity_main)

        val s = findViewById<Button>(R.id.s)
        s.text = GetSettings(applicationContext).load("s")
        val po = findViewById<Button>(R.id.po)
        po.text = GetSettings(applicationContext).load("po")

        if(starter==""){
            val button = findViewById<Button>(R.id.show_translete)
            button.visibility = View.GONE
            findViewById<Button>(R.id.next_word).text = "Старт!"
        } else {
            findViewById<Button>(R.id.show_translete).visibility
        }
        val wordHidden = findViewById<TextView>(R.id.word_hidden)
        val switch = findViewById<Switch>(R.id.change_language)
        switch.text = switchToggleValue
        setMainTextView()

        wordHidden.text = "***"
        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.settings_alert, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.but_delete).setOnClickListener {
                if (staticword.ru!="***"){
                    viewModel.delete(staticword)
                    Toast.makeText(this,"Слово удалено!",Toast.LENGTH_SHORT).show()
                    getNextWord()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this,"Слово не выбрано!",Toast.LENGTH_SHORT).show()
                }

            }
            dialogView.findViewById<Button>(R.id.but_update).setOnClickListener {
                if (staticword.ru!="***") {
                    val intent = Intent(this, AddActivity::class.java)
                    intent.putExtra("update", "")
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Слово не выбрано!",Toast.LENGTH_SHORT).show()
                }
            }

            dialogView.findViewById<ImageView>(R.id.exit_alert).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        findViewById<Button>(R.id.show_translete).setOnClickListener{
            if(switchToggleValue=="en"){
                wordHidden.text = staticword.ru
            } else {
                wordHidden.text = staticword.en
            }
        }

        findViewById<Button>(R.id.next_word).setOnClickListener {
            getNextWord()
        }

        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                switchToggleValue = "en"
                switch.text = "en"
            } else {
                switchToggleValue = "ru"
                switch.text = "ru"
            }
            setMainTextView()
            setHiddenTextView()
        }

        findViewById<Button>(R.id.goToAdd).setOnClickListener{
            startActivity(Intent(this@MainActivity,AddActivity::class.java))
        }



        s.setOnClickListener {
            showDatePickerDialogS()
        }

        po.setOnClickListener {
            showDatePickerDialogPo()
        }
    }

    private fun showDatePickerDialogS() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Создание DatePickerDialog с использованием Builder
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Обработка выбранной даты
                val selectedDateS = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                // Здесь вы можете использовать выбранную дату в своем приложении
                findViewById<Button>(R.id.s).text = selectedDateS
                GetSettings(applicationContext).save("s",selectedDateS)
            },
            year,
            month,
            day
        )

        // Показать диалог выбора даты
        datePickerDialog.show()
    }

    private fun showDatePickerDialogPo() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Создание DatePickerDialog с использованием Builder
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Обработка выбранной даты
                val selectedDatePo = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                // Здесь вы можете использовать выбранную дату в своем приложении
                findViewById<Button>(R.id.po).text = selectedDatePo
                GetSettings(applicationContext).save("po",selectedDatePo)
            },
            year,
            month,
            day
        )

        // Показать диалог выбора даты
        datePickerDialog.show()
    }

    private fun setMainTextView(){
        if(switchToggleValue=="en"){
            findViewById<TextView>(R.id.word_main).text = staticword.en
        } else {
            findViewById<TextView>(R.id.word_main).text = staticword.ru
        }
    }

    private fun setHiddenTextView(){
        var textView = findViewById<TextView>(R.id.word_hidden)
        if(textView.text!="***"){
            if(switchToggleValue=="en"){
                textView.text = staticword.ru
            } else {
                textView.text = staticword.en
            }
        }
    }

    private fun getNextWord(){
        starter = "start!"
        val wordDao = AppDatabase.getInstance(applicationContext).wordDao()
        val viewModel = ViewModel(wordDao)
        val dateFormat = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())
        val dates = dateFormat.parse(GetSettings(applicationContext).load("s"))
        val dateE = dateFormat.parse(GetSettings(applicationContext).load("po"))
        val dateStart = dates?.time ?: 0L
        val dateEnd = dateE?.time ?: 0L
        viewModel.getRandome(dateStart, dateEnd)
        recreate()
    }
}