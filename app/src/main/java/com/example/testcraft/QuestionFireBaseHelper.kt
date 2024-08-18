package com.example.testcraft

import com.google.firebase.firestore.FirebaseFirestore



class QuestionFireBaseHelper {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun saveQuestion(
        photoPreview: String,
        photoRating: String,
        examTitle: String,
        lessonTitle: String,
        answerChoices: String,
        photoNotes: String,
        callback: (Boolean) -> Unit

    ) {
        val questionData = mapOf(
            "photo_url" to photoPreview,
            "photo_rating" to photoRating,
            "exam_title" to examTitle,
            "lesson_title" to lessonTitle,
            "answer_choices" to answerChoices,
            "photo_notes" to photoNotes
        )

        firestore.collection("questions")
            .add(questionData)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
            }
    }



