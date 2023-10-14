package ru.vocabulary

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import ru.vocabulary.activity.AddActivity
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.Word

class MainActivity : AppCompatActivity() {

    companion object{
         var starter:String = ""
         var staticword :Word = Word(0,"***","***",0)
         var switchToggleValue:String = "ru"
         var staticCount:String = "0"
    }
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        findViewById<Button>(R.id.show_translete).setOnClickListener{
            if(switchToggleValue=="en"){
                wordHidden.text = staticword.ru
            } else {
                wordHidden.text = staticword.en
            }
        }

        findViewById<Button>(R.id.next_word).setOnClickListener {
            starter = "start!"
            val wordDao = AppDatabase.getInstance(applicationContext).wordDao()
            var viewModel = ViewModel(wordDao)
            viewModel.getRandome()
            recreate()
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
    }

    fun setMainTextView(){
        if(switchToggleValue=="en"){
            findViewById<TextView>(R.id.word_main).text = staticword.en
        } else {
            findViewById<TextView>(R.id.word_main).text = staticword.ru
        }
    }

    fun setHiddenTextView(){
        var textView = findViewById<TextView>(R.id.word_hidden)
        if(textView.text!="***"){
            if(switchToggleValue=="en"){
                textView.text = staticword.ru
            } else {
                textView.text = staticword.en
            }
        }
    }
}