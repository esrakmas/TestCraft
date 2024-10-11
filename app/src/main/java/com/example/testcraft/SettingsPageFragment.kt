package com.example.testcraft

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testcraft.loginandsignup.LoginSignupPageActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsPageFragment : Fragment() {

    private lateinit var settingsRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var examTitles: List<String> = listOf() // Sınav başlıklarını burada tutacağız
    private var selectedExamTitle: String? = null // Seçilen sınav başlığı
    private var courseTitles: List<String> = listOf() // Ders başlıklarını burada tutacağız
    private lateinit var pagerAdapter: TestPageAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_page, container, false)

        settingsRecyclerView = view.findViewById(R.id.settingsRecyclerView)
        settingsRecyclerView.layoutManager = LinearLayoutManager(context)

        auth = FirebaseAuth.getInstance()

        val settingsOptions = listOf(
            "Şifre Değiştir",
            "Sınav Başlığı Sil",
            "Ders Başlığı Sil",
            "Hesabımı Sil"
        )

        settingsRecyclerView.adapter = SettingsPageAdapter(settingsOptions) { option ->
            when (option) {
                "Şifre Değiştir" -> {
                    showPasswordChangeDialog()
                }
                "Sınav Başlığı Sil" -> {
                    showExamTitleDeleteDialog()
                }
                "Ders Başlığı Sil" -> {
                    showCourseTitleDeleteDialog()
                }
                "Hesabımı Sil" -> {
                    deleteAccount()
                }
            }
        }

        fetchExamTitles() // Sınav başlıklarını fetch et
        return view
    }

    // Sınav başlıklarını Firestore'dan çekme
    private fun fetchExamTitles() {
        firestore.collection("questions").get().addOnSuccessListener { documents ->
            examTitles = documents.mapNotNull { it.getString("exam_title") }.distinct()
        }.addOnFailureListener {
            showToast("Sınav başlıkları alınırken hata oluştu.")
        }
    }

    // Şifre değiştirme işlemi
    private fun showPasswordChangeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)

        val currentPasswordEditText: EditText = dialogView.findViewById(R.id.etCurrentPassword)
        val newPasswordEditText: EditText = dialogView.findViewById(R.id.etNewPassword)
        val confirmNewPasswordEditText: EditText = dialogView.findViewById(R.id.etConfirmNewPassword)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Şifre Değiştir")
            .setView(dialogView)
            .setNegativeButton("İptal", null)

        dialogBuilder.setPositiveButton("Şifreyi Değiştir") { _, _ ->
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmNewPassword = confirmNewPasswordEditText.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                showToast("Lütfen tüm alanları doldurun.")
                return@setPositiveButton
            }

            if (newPassword != confirmNewPassword) {
                showToast("Yeni şifreler uyuşmuyor.")
                return@setPositiveButton
            }

            changePassword(currentPassword, newPassword)
        }

        dialogBuilder.create().show()
    }

    // Şifreyi değiştirme işlemi
    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser

        if (user != null) {
            val credentials = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credentials).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            showToast("Şifre başarıyla değiştirildi.")
                        } else {
                            showToast("Şifre değiştirilemedi: ${updateTask.exception?.message}")
                        }
                    }
                } else {
                    showToast("Mevcut şifre hatalı.")
                }
            }
        }
    }

    // Sınav başlığı silme işlemi
    private fun showExamTitleDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sınav Başlığı Sil")

        val titlesArray = examTitles.toTypedArray()
        builder.setItems(titlesArray) { _, which ->
            val selectedTitle = titlesArray[which]
            showConfirmationDialogForExam(selectedTitle)
        }

        builder.setNegativeButton("İptal") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Sınav başlığını silmek için onay diyalogu
    private fun showConfirmationDialogForExam(title: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Onay")
        builder.setMessage("$title başlığını silmek istediğinize emin misiniz?")

        builder.setPositiveButton("Evet") { _, _ -> deleteExamTitle(title) }
        builder.setNegativeButton("Hayır") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Sınav başlığını silme işlemi
    private fun deleteExamTitle(title: String) {
        firestore.collection("questions")
            .whereEqualTo("exam_title", title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                showToast("Sınav başlığı başarıyla silindi.")
            }
            .addOnFailureListener {
                showToast("Sınav başlığı silinirken hata oluştu.")
            }
    }

    // Ders başlıklarını silme işlemi
    private fun showCourseTitleDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ders Başlığı Sil")

        val titlesArray = examTitles.toTypedArray()
        builder.setItems(titlesArray) { _, which ->
            selectedExamTitle = titlesArray[which]
            fetchCourseTitlesForSelectedExam() // Seçilen sınav başlığına ait dersleri getir
        }

        builder.setNegativeButton("İptal") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Seçilen sınav başlığına ait dersleri Firestore'dan al
// Seçilen sınav başlığına ait dersleri Firestore'dan al
    private fun fetchCourseTitlesForSelectedExam() {
        selectedExamTitle?.let {
            firestore.collection("courses")
                .whereEqualTo("exam_title", it)  // Burada sınav başlığına göre sorgulama yapılıyor
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        showToast("Bu sınav başlığına ait ders bulunamadı.")
                    } else {
                        // Ders başlıklarını mapleyip courseTitles'a atıyoruz
                        courseTitles = documents.mapNotNull { it.getString("course_title") }.distinct()
                        // Dersleri göstermek için ilgili fonksiyonu çağır
                        showCourseTitleDeleteDialogWithCourses()
                    }
                }
                .addOnFailureListener {
                    showToast("Ders başlıkları alınırken hata oluştu: ${it.message}")
                }
        }
    }


    // Ders başlıklarını gösteren diyalog
    private fun showCourseTitleDeleteDialogWithCourses() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ders Başlıklarını Sil")

        val titlesArray = courseTitles.toTypedArray()
        builder.setItems(titlesArray) { _, which ->
            val selectedCourse = titlesArray[which]
            showConfirmationDialogForCourse(selectedCourse)
        }

        builder.setNegativeButton("İptal") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Ders silme işlemi için onay diyalogu
    private fun showConfirmationDialogForCourse(course: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Onay")
        builder.setMessage("$course dersini silmek istediğinize emin misiniz?")

        builder.setPositiveButton("Evet") { _, _ -> deleteCourse(course) }
        builder.setNegativeButton("Hayır") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Seçilen dersi silme işlemi
    private fun deleteCourse(course: String) {
        firestore.collection("courses")
            .whereEqualTo("course_title", course)
            .whereEqualTo("exam_title", selectedExamTitle)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                showToast("$course dersi başarıyla silindi.")
            }
            .addOnFailureListener {
                showToast("Ders silinirken hata oluştu.")
            }
    }

    // Hesap silme işlemi
    private fun deleteAccount() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Hesap Silme")
        builder.setMessage("Hesabınızı silmek istediğinizden emin misiniz? Bütün bilgileriniz kalıcı olarak silinecektir.")
        builder.setPositiveButton("Evet") { _, _ ->
            val user = auth.currentUser
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Hesap başarıyla silindi.")
                        val intent = Intent(requireContext(), LoginSignupPageActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        showToast("Hesap silinirken hata oluştu: ${task.exception?.message}")
                    }
                }
        }
        builder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
