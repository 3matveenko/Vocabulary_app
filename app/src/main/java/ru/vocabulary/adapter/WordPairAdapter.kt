package ru.vocabulary.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import ru.vocabulary.R
import ru.vocabulary.model.WordPair

class WordPairAdapter(private val wordPairs: MutableList<WordPair>) : RecyclerView.Adapter<WordPairAdapter.WordPairViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordPairViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word_pair, parent, false)
        return WordPairViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordPairViewHolder, position: Int) {
        holder.bind(wordPairs[position])
    }

    override fun getItemCount(): Int = wordPairs.size

    fun getUpdatedWordPairs(): List<WordPair> {
        return wordPairs
    }

    inner class WordPairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ruWordEditText: EditText = itemView.findViewById(R.id.ruWordEditText)
        private val enWordEditText: EditText = itemView.findViewById(R.id.enWordEditText)
        private val deleteItemButton: ImageButton = itemView.findViewById(R.id.deleteItemButton)

        // These watchers are now created from a concrete class
        private val ruTextWatcher = SimpleTextWatcher { position, text ->
            if (position < wordPairs.size) wordPairs[position] = wordPairs[position].copy(ru = text)
        }
        private val enTextWatcher = SimpleTextWatcher { position, text ->
            if (position < wordPairs.size) wordPairs[position] = wordPairs[position].copy(en = text)
        }

        init {
            ruWordEditText.addTextChangedListener(ruTextWatcher)
            enWordEditText.addTextChangedListener(enTextWatcher)
        }

        fun bind(wordPair: WordPair) {
            // Remove old listeners to prevent them from firing on the wrong items
            ruWordEditText.removeTextChangedListener(ruTextWatcher)
            enWordEditText.removeTextChangedListener(enTextWatcher)

            // Set text FIRST
            ruWordEditText.setText(wordPair.ru)
            enWordEditText.setText(wordPair.en)

            // Update watcher positions
            ruTextWatcher.updatePosition(adapterPosition)
            enTextWatcher.updatePosition(adapterPosition)

            // Add the listeners back
            ruWordEditText.addTextChangedListener(ruTextWatcher)
            enWordEditText.addTextChangedListener(enTextWatcher)

            deleteItemButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    wordPairs.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, wordPairs.size) // To update positions
                }
            }
        }
    }
}

// A helper class to simplify TextWatcher (no longer abstract)
class SimpleTextWatcher(private val onTextChanged: (position: Int, text: String) -> Unit) : TextWatcher {
    private var position: Int = 0

    fun updatePosition(position: Int) {
        this.position = position
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged.invoke(position, s.toString())
    }

    override fun afterTextChanged(s: Editable?) {}
}