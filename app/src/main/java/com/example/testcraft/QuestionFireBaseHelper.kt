package com.example.testcraft

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore



class QuestionFireBaseHelper(private val activity: Activity) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //soru kaydetme
    fun saveQuestion(
        photoUrl: String,
        photoRating: Double,
        examTitle: String,
        lessonTitle: String,
        answerChoices: String,
        photoNotes: String

    ) {
        val questionData = hashMapOf(
            "photo_url" to photoUrl,
            "photo_rating" to photoRating,
            "exam_title" to examTitle,
            "lesson_title" to lessonTitle,
            "answer_choices" to answerChoices,
            "photo_notes" to photoNotes
        )

        firestore.collection("questions")
            .add(questionData)
            .addOnSuccessListener {
                showToast("Soru başarıyla kaydedildi!")
            }
            .addOnFailureListener { e ->
                showToast("Soru kaydedilirken hata oluştu: ${e.message}")
            }



    }




    //Firestore'dan exam_title Verileri Çekme
    fun fetchGroupsByExamTitle (callback: (Map<String, List<Map<String, Any>>>) -> Unit) {
        firestore.collection("questions")
            .get()
            .addOnSuccessListener { documents ->
                //Bu Map, sınav başlıklarını (String türünde anahtarlar) ve her sınav başlığına
                // ait ders verilerini içeren bir listeyi (List<Map<String, Any>>) içerir.
                val groupsExams = mutableMapOf<String, MutableList<Map<String, Any>>>()
                Log.d("FirebaseData", "Documents fetched: ${documents.size()}")

                for (document in documents) {
                    val examTitle = document.getString("exam_title") ?: continue
                    val questionData = document.data

                    Log.d("FirebaseData", "Exam Title: $examTitle, Question Data: $questionData")

                    if (!groupsExams.containsKey(examTitle)) {
                        groupsExams[examTitle] = mutableListOf()
                    }
                    groupsExams[examTitle]?.add(questionData)
                }
                Log.d("FirebaseData", "Grouped Data: $groupsExams")
                callback(groupsExams)
            }
            .addOnFailureListener { e ->
                showToast("Sınav adları getirilirken hata oluştu: ${e.message}")
                Log.e("FirebaseError", "Error fetching documents", e)
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}



