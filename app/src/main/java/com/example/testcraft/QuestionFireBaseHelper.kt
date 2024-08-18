package com.example.testcraft

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore



class QuestionFireBaseHelper(private val activity: Activity) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun saveQuestion(
        photoUrl: String,
        photoRating: String,
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


    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}



