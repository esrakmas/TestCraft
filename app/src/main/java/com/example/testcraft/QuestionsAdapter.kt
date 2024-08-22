package com.example.testcraft

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class QuestionsAdapter(private val questions: List<Map<String, Any?>>) :
    RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {


    // ViewHolder sınıfı
    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView2)
        private val textView: TextView = itemView.findViewById(R.id.textView4)
        private val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroup)
        private val buttonInfo: Button = itemView.findViewById(R.id.button)
        private val buttonArchive: Button = itemView.findViewById(R.id.button2)

        fun bind(question: Map<String, Any?>) {
            // Verileri bağla
            textView.text = question["question_text"].toString()
            // Örnek olarak, resim ve butonlar için veri ayarlamayı buraya ekleyin
            // ...
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }


    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int = questions.size
}
