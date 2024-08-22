package com.example.testcraft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TestListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var testListAdapter: TestListAdapter
    private lateinit var firebaseHelper: QuestionFireBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list)


        recyclerView = findViewById(R.id.recyclerViewTestlist)
        testListAdapter = TestListAdapter()
        recyclerView.adapter = testListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // LayoutManager'ı ayarlayın



        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: ""
        val lessonTitle = intent.getStringExtra("LESSON_TITLE") ?: ""

        if (examTitle.isEmpty() || lessonTitle.isEmpty()) {
            Log.e("gruplama", "Exam title or lesson title is null or empty")
        } else {
            Log.d("TestListActivity", "Received exam title: $examTitle")
            Log.d("TestListActivity", "Received lesson title: $lessonTitle")
        }




        firebaseHelper = QuestionFireBaseHelper(this)

        firebaseHelper.fetchGroupsByExamTitle { groupedData ->


            Log.d("gruplama", "Grouped data: $groupedData")


            if (examTitle == null || lessonTitle == null) {
                Log.d("gruplama", "Exam title or lesson title is null")

            }

            Log.d("gruplama", "Exam title: $examTitle")
            Log.d("gruplama", "Lesson title: $lessonTitle")

            // Seçilen examTitle ve lessonTitle verilerini filtreleyin
            val selectedLessonData = groupedData[examTitle]?.filter {
                it["lesson_title"] == lessonTitle
            }
            Log.d("gruplama", "Selected lesson data: $selectedLessonData")

            // Üçlü (exam_title, lesson_title, photoRating) grup oluşturma
            val groupedByExamLessonAndRating = selectedLessonData?.groupBy {
                Triple(
                    it["exam_title"] as? String,
                    it["lesson_title"] as? String,
                    it["photo_rating"] as? Double
                )
            }
            Log.d("gruplama", "Data grouped by exam, lesson, and rating: $groupedByExamLessonAndRating")

            // Adapter'a gruplandırılmış verileri gönderin
            testListAdapter.updateData(groupedByExamLessonAndRating)
        }



    }
}
