package com.example.testcraft

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    private lateinit var settingsRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var examTitles: List<String> = listOf() // Sınav başlıklarını burada tutacağız
    private lateinit var pagerAdapter: TestPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsRecyclerView = view.findViewById(R.id.settingsRecyclerView)
        settingsRecyclerView.layoutManager = LinearLayoutManager(context)

        val settingsOptions = listOf(
            "Şifre Değiştir",
            "Sınav Başlığı Sil",
            "Ders Başlığı Sil",
            "Hesabımı Sil"
        )

        settingsRecyclerView.adapter = SettingsAdapter(settingsOptions) { option ->
            when (option) {
                "Şifre Değiştir" -> {
                    // Şifre değiştirme işlemi
                    showToast("Şifre değiştirme işlemi başarılı.")
                }
                "Sınav Başlığı Sil" -> {
                    showExamTitleDeleteDialog()
                }
                "Ders Başlığı Sil" -> {
                    // Ders başlığı silme işlemi
                    showToast("Ders başlığı silme işlemi başarılı.")
                }
                "Hesabımı Sil" -> {
                    // Hesabı silme işlemi
                    showToast("Hesap silme işlemi başarılı.")
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

    private fun showExamTitleDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sınav Başlığı Sil")

        val titlesArray = examTitles.toTypedArray()
        builder.setItems(titlesArray) { _, which ->
            val selectedTitle = titlesArray[which]
            showConfirmationDialog(selectedTitle)
        }

        builder.setNegativeButton("İptal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showConfirmationDialog(title: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Onay")
        builder.setMessage("$title başlığını silmek istediğinize emin misiniz?")

        builder.setPositiveButton("Evet") { _, _ ->
            deleteExamTitle(title)
        }

        builder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }

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
        // Eğer ViewPager2'yi doğrudan etkiliyorsanız, burada gerekli işlemi yapmalısınız
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
