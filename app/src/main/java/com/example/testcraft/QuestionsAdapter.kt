package com.example.testcraft

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionsAdapter(
    private val questions: List<Map<String, Any?>>
) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView2)
        private val textView: TextView = itemView.findViewById(R.id.textView4)
        private val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroup)
        private val buttonInfo: Button = itemView.findViewById(R.id.button)
        private val buttonArchive: Button = itemView.findViewById(R.id.button2)

        fun bind(question: Map<String, Any?>) {
            textView.text = question["question_text"].toString()
            // Diğer verileri bağlayın

            // buttonInfo butonuna tıklama olayını tanımlayın
            buttonInfo.setOnClickListener {
                showPhotoNotesDialog(itemView.context, question["photoNotes"].toString())
            }
        }

        private fun showPhotoNotesDialog(context: Context, photoNotes: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Photo Notes")
            builder.setMessage(photoNotes)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
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
