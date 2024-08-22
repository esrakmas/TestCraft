package com.example.testcraft


import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import com.example.testcraft.databinding.AddQuestionDialogLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore

//

class AddQuestionDialogHelper(private val activity: Activity) {

    private val photoHandler = PhotoHandler(activity)
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





        // Exam ve Lesson verilerini spinner'lara yükleme

        fetchExam { titlesList ->
            updateSpinner(examSpinner, titlesList)
        }

        fetchLesson { titlesList ->
            updateSpinner(lessonSpinner, titlesList)
        }

        // tıklama olayları
        fromgallery.setOnClickListener {
            photoHandler.openGallery()
            Toast.makeText(activity, "Galeriden yükle", Toast.LENGTH_SHORT).show()
        }

        takephoto.setOnClickListener {
            Toast.makeText(activity, "Foto çek", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        val photoUrlImageView = dialog.findViewById<ImageView>(R.id.photo_preview)
        val photoRatingBar = dialog.findViewById<RatingBar>(R.id.photo_rating)
        val examTitleSpinner = dialog.findViewById<Spinner>(R.id.exam_spinner)
        val lessonTitleSpinner = dialog.findViewById<Spinner>(R.id.lesson_spinner)
        val photoNotesEditText = dialog.findViewById<EditText>(R.id.photo_notes)


        val answerChoicesGroup = dialog.findViewById<RadioGroup>(R.id.answer_choices)


        saveQuestionButton.setOnClickListener{
            // Form doğrulaması yap
            val photoUrl = photoUrlImageView.tag?.toString() ?: "default_url"
            val photoRating = photoRatingBar.rating.toDouble()
            val examTitle = examTitleSpinner.selectedItem.toString()
            val lessonTitle = lessonTitleSpinner.selectedItem.toString()
            val photoNotes = photoNotesEditText.text.toString()
            val answerChoices = getSelectedAnswerChoice(answerChoicesGroup)


            // Hataları toplamak için bir liste oluştur
            val errorMessages = mutableListOf<String>()


            // Gerekli alanların kontrolünü yap
            if (examTitle.isEmpty()) {
                errorMessages.add("Lütfen sınav türünü seçiniz.")
            }
            if (photoRating ==0.0) {
                errorMessages.add("Lütfen sorunun seviyesini seçiniz.")
            }
            if (lessonTitle.isEmpty()) {
                errorMessages.add("Lütfen ders türünü seçiniz.")
            }
            if (answerChoices == "No answer selected") {
                errorMessages.add("Lütfen sorunun cevabını seçiniz.")
            }

            if (errorMessages.isNotEmpty()) {
                // Uyarı mesajını oluştur ve göster
                val errorMessage = errorMessages.joinToString("\n")
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }else{

                questionFireBaseHelper.saveQuestion(
                    photoUrl = photoUrlImageView.tag?.toString() ?: "default_url",
                    photoRating = photoRatingBar.rating.toDouble(),
                    examTitle = examTitleSpinner.selectedItem.toString(),
                    lessonTitle = lessonTitleSpinner.selectedItem.toString(),
                    photoNotes = photoNotesEditText.text.toString(),
                    answerChoices = getSelectedAnswerChoice(answerChoicesGroup)
                )

                 }
        }

        dialog.show()

    }

    private fun getSelectedAnswerChoice(answerChoices: RadioGroup): String {
        val selectedId = answerChoices.checkedRadioButtonId
        return if (selectedId != -1) {
            val selectedRadioButton = answerChoices.findViewById<RadioButton>(selectedId)
            selectedRadioButton.text.toString()
        } else {
            "No answer selected"
        }
    }


    //spinner yükleme fonksiyonları

    private fun fetchExam(callback: (List<String>) -> Unit) {
        db.collection("exams")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val titlesList = mutableListOf<String>()
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        for (document in documents) {
                            // Iterate over all values in the document
                            val data = document.data
                            for (value in data.values) {
                                titlesList.add(value.toString()) // Add only the value to the list
                            }
                        }
                        callback(titlesList)
                    }
                } else {
                    Toast.makeText(activity, "Belgeler alınırken hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun fetchLesson(callback: (List<String>) -> Unit) {
        db.collection("lessons")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val titlesList = mutableListOf<String>()
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        for (document in documents) {
                            // Iterate over all values in the document
                            val data = document.data
                            for (value in data.values) {
                                titlesList.add(value.toString()) // Add only the value to the list
                            }
                        }
                        callback(titlesList)
                    }
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