package com.example.testcraft.loginandsignup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testcraft.HomePageActivity
import com.example.testcraft.databinding.FragmentLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginPageFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View binding başlatılıyor
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase Authentication başlat
        auth = FirebaseAuth.getInstance()

        // Giriş yap butonuna tıklanma olayını dinle
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            // E-posta ve şifrenin boş olup olmadığını kontrol et
            if (email.isEmpty()) {
                Toast.makeText(context, "Lütfen e-posta adresinizi girin", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(context, "Lütfen şifrenizi girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kullanıcıyı giriş yap
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Giriş başarılı
                        Toast.makeText(context, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                        // Anasayfaya yönlendirme veya başka bir işlem yapabilirsiniz
                        // HomePageActivity'ye geçiş yap
                        val intent = Intent(activity, HomePageActivity::class.java)
                        startActivity(intent)

                        // Fragment'i kapatmak için activity'yi bitir
                        activity?.finish()
                    } else {
                        // Giriş başarısız
                        Toast.makeText(
                            context,
                            "Giriş Başarısız: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Binding temizleniyor
    }
}
