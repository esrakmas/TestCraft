package com.example.testcraft

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LessonAdapter(private val lessonList: MutableList<Map<String, Any>> = mutableListOf()) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    private val uniqueLessonTitles = mutableSetOf<String>()

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lessonTitleTextView: TextView = itemView.findViewById(R.id.lessonTitleTextView)

        fun bind(lesson: Map<String, Any>) {
            val lessonTitle = lesson["lesson_title"].toString()
            lessonTitleTextView.text = lessonTitle

            itemView.setOnClickListener {
                Log.d("LessonAdapter", "Clicked on lesson: $lessonTitle")
                val context = itemView.context
                val intent = Intent(context, TestListActivity::class.java)
                // Diğer verileri de intent'e ekleyin
                intent.putExtra("EXAM_TITLE", lesson["exam_title"].toString())
                intent.putExtra("LESSON_TITLE", lessonTitle)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessonList[position]
        holder.bind(lesson)  // Bind fonksiyonunu burada çağırıyoruz
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    // Benzersiz lesson_title'ları ekleyen fonksiyon
    fun setLessons(lessons: List<Map<String, Any>>) {
        for (lesson in lessons) {
            val lessonTitle = lesson["lesson_title"]?.toString()?.trim()?.lowercase()
            if (lessonTitle != null && uniqueLessonTitles.add(lessonTitle)) {
                lessonList.add(lesson)
            }
        }
        notifyDataSetChanged()
    }
}
