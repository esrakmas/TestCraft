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
                    showToast("Ders başlığı silme işlemi başarılı.")
                }
                "Hesabımı Sil" -> {
                    deleteAccount()
                }
            }
        }

        fetchExamTitles() // Sınav başlıklarını fetch et
        return view
    }

    private fun fetchExamTitles() {
        firestore.collection("questions").get().addOnSuccessListener { documents ->
            examTitles = documents.mapNotNull { it.getString("exam_title") }.distinct()
        }.addOnFailureListener {
            showToast("Sınav başlıkları alınırken hata oluştu.")
        }
    }

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

    private fun showExamTitleDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sınav Başlığı Sil")

        val titlesArray = examTitles.toTypedArray()
        builder.setItems(titlesArray) { _, which ->
            val selectedTitle = titlesArray[which]
            showConfirmationDialog(selectedTitle)
        }

        builder.setNegativeButton("İptal") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showConfirmationDialog(title: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Onay")
        builder.setMessage("$title başlığını silmek istediğinize emin misiniz?")

        builder.setPositiveButton("Evet") { _, _ -> deleteExamTitle(title) }
        builder.setNegativeButton("Hayır") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun deleteExamTitle(title: String) {
        firestore.collection("questions")
            .whereEqualTo("exam_title", title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                updateTabsAfterDeletion(title)
                showToast("Sınav başlığı başarıyla silindi.")
            }
            .addOnFailureListener {
                showToast("Sınav başlığı silinirken hata oluştu.")
            }
    }

    private fun updateTabsAfterDeletion(title: String) {
        // Bu işlem, TestPagerAdapter'ı yeniden oluşturmayı ve ViewPager2'yi güncellemeyi içerebilir
    }

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
                        // Kullanıcıyı giriş ekranına yönlendirebilirsiniz
                        // Giriş ekranına yönlendirmek için örneğin:
                        // startActivity(Intent(requireContext(), LoginActivity::class.java))
                        val intent = Intent(requireContext(), LoginSignupPageActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        showToast("Hesap silinirken hata oluştu: ${task.exception?.message}")
                    }
                }
        }
        builder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss() // Kullanıcı silme işleminden vazgeçerse diyaloğu kapat
        }

        builder.create().show()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
