package com.example.testcraft

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson





class QuestionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var questionsAdapter: QuestionsAdapter
    private var questions: List<Map<String, Any?>> = listOf()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)


        val jsonData = intent.getStringExtra("QUESTION_DATA") ?: "[]"


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        questionsAdapter = QuestionsAdapter(questions)
        recyclerView.adapter = questionsAdapter


        showQuestion(currentIndex)

        findViewById<Button>(R.id.prevButton).setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showQuestion(currentIndex)
            }
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion(currentIndex)
            }
        }
    }

    private fun showQuestion(index: Int) {
        questionsAdapter.notifyDataSetChanged() // Adapter'a veri güncellenmiş olduğunu bildirir
        recyclerView.scrollToPosition(index) // Sorunun görünür olduğunu garanti eder
    }
}
