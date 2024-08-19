package com.example.testcraft

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView




class LessonAdapter(private val lessonList: MutableList<Map<String, Any>> = mutableListOf()) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    private val uniqueLessonTitles = mutableSetOf<String>()

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lessonTitle: TextView = view.findViewById(R.id.lessonTitleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessonList[position]
        holder.lessonTitle.text = lesson["lesson_title"].toString()
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

