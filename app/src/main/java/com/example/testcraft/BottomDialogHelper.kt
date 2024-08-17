package com.example.testcraft

import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class BottomDialogHelper(private val activity: Activity) {
    private val db = FirebaseFirestore.getInstance()

    fun showBottomDialog() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_question_layout)

        // Set the dialog's width to 90% of the screen width
        dialog.window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val fromgallery = dialog.findViewById<LinearLayout>(R.id.fromgallery)
        val takephoto = dialog.findViewById<LinearLayout>(R.id.takephoto)
        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)
        val examSpinner = dialog.findViewById<Spinner>(R.id.exam_spinner)

        fetchTitles { titlesList ->
            updateSpinner(examSpinner, titlesList)
        }

        fromgallery.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(activity, "Galeriden yükle", Toast.LENGTH_SHORT).show()
        }

        takephoto.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(activity, "Foto çek", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchTitles(callback: (List<String>) -> Unit) {
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


    private fun updateSpinner(spinner: Spinner, titlesList: List<String>) {
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, titlesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
