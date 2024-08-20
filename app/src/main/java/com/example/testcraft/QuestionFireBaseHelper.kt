package com.example.testcraft

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore



class QuestionFireBaseHelper(private val activity: Activity) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //soru kaydetme
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


        groupQuestions(photoUrl, photoRating, examTitle, lessonTitle, answerChoices, photoNotes)

    }
    // Soruları uygun koleksiyonlara gruplama
    private fun groupQuestions(
        photoUrl: String,
        photoRating: String,
        examTitle: String,
        lessonTitle: String,
        answerChoices: String,
        photoNotes: String
    ) {
        val maxQuestions = 5
        val questionsRef = firestore.collection("Tests")
            .document(examTitle)
            .collection(lessonTitle)
            .document(photoRating)

        questionsRef.get()
            .addOnSuccessListener { snapshot ->
                var currentCollectionNumber = 1
                var currentCollectionName = "Test$currentCollectionNumber"

                fun checkCollection(collectionName: String, onComplete: (Boolean) -> Unit) {
                    questionsRef.collection(collectionName)
                        .get()
                        .addOnSuccessListener { collectionSnapshot ->
                            onComplete(collectionSnapshot.size() < maxQuestions)
                        }
                        .addOnFailureListener { e ->
                            showToast("Koleksiyonlar alınırken hata oluştu: ${e.message}")
                        }
                }

                fun findSuitableCollection() {
                    checkCollection(currentCollectionName) { isSuitable ->
                        if (isSuitable) {
                            val newQuestionRef = questionsRef.collection(currentCollectionName)
                                .document()

                            val questionData = hashMapOf(
                                "photo_url" to photoUrl,
                                "photo_rating" to photoRating,
                                "exam_title" to examTitle,
                                "lesson_title" to lessonTitle,
                                "answer_choices" to answerChoices,
                                "photo_notes" to photoNotes
                            )

                            newQuestionRef.set(questionData)
                                .addOnSuccessListener {
                                    showToast("Soru başarıyla kaydedildi!")
                                }
                                .addOnFailureListener { e ->
                                    showToast("Soru kaydedilirken hata oluştu: ${e.message}")
                                }
                        } else {
                            currentCollectionNumber++
                            currentCollectionName = "Test$currentCollectionNumber"
                            findSuitableCollection()
                        }
                    }
                }

                findSuitableCollection()
            }
            .addOnFailureListener { e ->
                showToast("Koleksiyonlar alınırken hata oluştu: ${e.message}")
            }
    }












    //Firestore'dan exam_title Verileri Çekme
    fun fetchQuestionsByExamTitle(callback: (Map<String, List<Map<String, Any>>>) -> Unit) {
        firestore.collection("questions")
            .get()
            .addOnSuccessListener { documents ->
                val groupedQuestions = mutableMapOf<String, MutableList<Map<String, Any>>>()

                for (document in documents) {
                    val examTitle = document.getString("exam_title") ?: continue
                    val questionData = document.data

                    if (!groupedQuestions.containsKey(examTitle)) {
                        groupedQuestions[examTitle] = mutableListOf()
                    }
                    groupedQuestions[examTitle]?.add(questionData)
                }

                callback(groupedQuestions)
            }
            .addOnFailureListener { e ->
                showToast("Sınav adları getirilirken hata oluştu: ${e.message}")
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}



