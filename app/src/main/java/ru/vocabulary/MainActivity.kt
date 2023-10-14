package ru.vocabulary

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import ru.vocabulary.activity.AddActivity
import ru.vocabulary.model.AppDatabase
import ru.vocabulary.model.ViewModel
import ru.vocabulary.model.Word

class MainActivity : AppCompatActivity() {

    companion object{
         var staticword :Word = Word(0,"***","***",0)
         var switchToggleValue:String = "en"
    }
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wordMain = findViewById<TextView>(R.id.word_main)
        val wordHidden = findViewById<TextView>(R.id.word_hidden)
        val switch = findViewById<Switch>(R.id.change_language)
        switch.text = switchToggleValue
        if(switchToggleValue=="en"){
            wordMain.text = staticword.en
        } else {
            wordMain.text = staticword.ru
        }

        wordHidden.text = "***"

        findViewById<Button>(R.id.show_translete).setOnClickListener{
            if(switchToggleValue=="en"){
                wordHidden.text = staticword.ru
            } else {
                wordHidden.text = staticword.en
            }
        }

        findViewById<Button>(R.id.next_word).setOnClickListener {
            val wordDao = AppDatabase.getInstance(applicationContext).wordDao()
            var viewModel = ViewModel(wordDao)
            viewModel.getRandome()
            recreate()
        }

        switch.setOnClickListener {
            if(switchToggleValue=="ru"){
                switchToggleValue = "en"
                switch.text = "en"
            } else{
                switchToggleValue = "ru"
                switch.text = "ru"
            }
            recreate()
        }

        findViewById<Button>(R.id.goToAdd).setOnClickListener{
            startActivity(Intent(this@MainActivity,AddActivity::class.java))
        }
    }
}