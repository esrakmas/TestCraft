package com.example.testcraft

import PhotoHandler
import PhotoManager
import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.example.testcraft.databinding.AddQuestionLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore

class BottomDialogHelper(private val activity: Activity) {

    private val photoHandler = PhotoHandler(activity)
    private val photoManager = PhotoManager(activity)

    private val db = FirebaseFirestore.getInstance()

    fun showBottomDialog() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_question_layout)


        // Binding kullanarak layout erişimi
        val binding = AddQuestionLayoutBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        // Dialog'un genişliğini ekranın %90'ı olacak şekilde ayarlıyoruz
        dialog.window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val fromgallery = dialog.findViewById<LinearLayout>(R.id.fromgallery)
        val takephoto = dialog.findViewById<LinearLayout>(R.id.takephoto)
        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)
        val examSpinner = dialog.findViewById<Spinner>(R.id.exam_spinner)
        val lessonSpinner = dialog.findViewById<Spinner>(R.id.lesson_spinner)
        val saveButton = dialog.findViewById<Button>(R.id.save_question_button)
        val photoPreview = binding.photoPreview




        // Exam ve Lesson verilerini spinner'lara yükleme

        fetchExam { titlesList ->
            updateSpinner(examSpinner, titlesList)
        }

        fetchLesson { titlesList ->
            updateSpinner(lessonSpinner, titlesList)
        }

        // Tıklama olayları
        fromgallery.setOnClickListener {
            dialog.dismiss()
            photoHandler.openGallery()
            Toast.makeText(activity, "Galeriden yükle", Toast.LENGTH_SHORT).show()
        }

        takephoto.setOnClickListener {
            dialog.dismiss()
            photoHandler.openCamera()
            Toast.makeText(activity, "Foto çek", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }






        dialog.show()
    }

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
                    Toast.makeText(activity, "Error getting documents: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, "Error getting documents: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun updateSpinner(spinner: Spinner, titlesList: List<String>) {
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, titlesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }





}