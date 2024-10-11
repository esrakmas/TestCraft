package com.example.testcraft

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.example.testcraft.databinding.AddQuestionDialogLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddQuestionDialogHelper(private val activity: Activity) {

    val photoHandler = PhotoHandler(activity)
    private val db = FirebaseFirestore.getInstance()
    private val questionFireBaseHelper = QuestionFireBaseHelper(activity)

    fun showBottomDialog() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Binding kullanarak layout erişimi
        val binding = AddQuestionDialogLayoutBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        // Dialog'un genişliğini ekranın %90'ı olacak şekilde ayarlıyoruz
        dialog.window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val fromgallery = binding.fromgallery
        val takephoto = binding.takephoto
        val cancelButton = binding.cancelButton
        val examSpinner = binding.examSpinner
        val lessonSpinner = binding.lessonSpinner
        val saveQuestionButton = binding.saveQuestionButton
        val photoPreview = binding.photoPreview
        photoHandler.setPhotoPreview(photoPreview)

        fromgallery.setOnClickListener {
            photoHandler.openGallery()
            Toast.makeText(activity, "Galeriden yükle", Toast.LENGTH_SHORT).show()
        }

        takephoto.setOnClickListener {
            photoHandler.openCamera()
            Toast.makeText(activity, "Fotoğraf çek", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // Exam ve Lesson verilerini spinner'lara yükleme
        fetchExam { titlesList -> updateSpinner(examSpinner, titlesList) }
        fetchLesson { titlesList -> updateSpinner(lessonSpinner, titlesList) }

        saveQuestionButton.setOnClickListener {
            // Form doğrulaması yap
            val photoUrl = photoPreview.tag?.toString() ?: "default_url"
            val photoRating = binding.photoRating.rating.toDouble()
            val examTitle = examSpinner.selectedItem.toString()
            val lessonTitle = lessonSpinner.selectedItem.toString()
            val photoNotes = binding.photoNotes.text.toString()
            val answerChoices = getSelectedAnswerChoice(binding.answerChoices)

            val errorMessages = mutableListOf<String>()

            if (examTitle.isEmpty()) errorMessages.add("Lütfen sınav türünü seçiniz.")
            if (photoRating == 0.0) errorMessages.add("Lütfen sorunun seviyesini seçiniz.")
            if (lessonTitle.isEmpty()) errorMessages.add("Lütfen ders türünü seçiniz.")
            if (answerChoices == "No answer selected") errorMessages.add("Lütfen sorunun cevabını seçiniz.")

            if (errorMessages.isNotEmpty()) {
                val errorMessage = errorMessages.joinToString("\n")
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            } else {
                questionFireBaseHelper.saveQuestion(photoUrl, photoRating, examTitle, lessonTitle, photoNotes, answerChoices)
            }
        }

        dialog.show()
    }

    private fun getSelectedAnswerChoice(answerChoices: RadioGroup): String {
        val selectedId = answerChoices.checkedRadioButtonId
        return if (selectedId != -1) {
            answerChoices.findViewById<RadioButton>(selectedId).text.toString()
        } else {
            "No answer selected"
        }
    }

    // Firestore'dan exam verilerini getir
    private fun fetchExam(callback: (List<String>) -> Unit) {
        db.collection("exams").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val titlesList = task.result?.mapNotNull { it.data.values.firstOrNull().toString() } ?: emptyList()
                callback(titlesList)
            } else {
                Toast.makeText(activity, "Belgeler alınırken hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firestore'dan lesson verilerini getir
    private fun fetchLesson(callback: (List<String>) -> Unit) {
        db.collection("lessons").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val titlesList = task.result?.mapNotNull { it.data.values.firstOrNull().toString() } ?: emptyList()
                callback(titlesList)
            } else {
                Toast.makeText(activity, "Belgeler alınırken hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSpinner(spinner: Spinner, titlesList: List<String>) {
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, titlesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
