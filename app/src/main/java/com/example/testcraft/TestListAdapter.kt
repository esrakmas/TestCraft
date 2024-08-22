package com.example.testcraft

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class TestListAdapter( private var groupedData: Map<Triple<String?, String?, Double?>, List<Map<String, Any>>>? = null) :
    RecyclerView.Adapter<TestListAdapter.TestGroupViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_testlist, parent, false)
        return TestGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestGroupViewHolder, position: Int) {
        // Grupları listeye çevirip her bir öğeyi alıyoruz
        val entry = groupedData?.entries?.toList()?.get(position)
        val rating = entry?.key?.third // Üçlü grubun photoRating kısmı
        val questions = entry?.value
        val examTitle = entry?.key?.first ?: ""
        val lessonTitle = entry?.key?.second ?: ""

        Log.d("AdapterData", "Position: $position, Rating: $rating, Questions: $questions")

        holder.bind(rating, questions, examTitle, lessonTitle)




    }

    override fun getItemCount(): Int = groupedData?.size ?: 0


    fun updateData(newGroupedData: Map<Triple<String?, String?, Double?>, List<Map<String, Any>>>?) {

        groupedData = newGroupedData
        notifyDataSetChanged()
    }



    class TestGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(rating: Double?, questions: List<Map<String, Any>>?, examTitle: String, lessonTitle: String) {
            Log.d("BindView", "Binding view with rating: $rating and questions: $questions")
            itemView.findViewById<TextView>(R.id.textViewTestTitle).text = "Test $rating"
            itemView.findViewById<RatingBar>(R.id.ratingBar).rating = rating?.toFloat() ?: 0f

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, QuestionsActivity::class.java).apply {
                    putExtra("EXAM_TITLE", examTitle)
                    putExtra("LESSON_TITLE", lessonTitle)
                    putExtra("PHOTO_RATING", rating)
                    // Veriyi JSON string olarak gönderebilirsiniz
                    val jsonData = Gson().toJson(questions)
                    putExtra("QUESTION_DATA", jsonData)


                }


                context.startActivity(intent)
            }



        }
    }
}