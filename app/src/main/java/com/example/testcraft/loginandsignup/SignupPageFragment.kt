package com.example.testcraft.loginandsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.testcraft.databinding.FragmentSignupPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignupPageFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSignupPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View binding başlatılıyor
        _binding = FragmentSignupPageBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase Authentication başlat
        auth = FirebaseAuth.getInstance()

        // Kayıt ol butonuna tıklanma olayını dinle
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()
            val confirmPassword = binding.signupConfirm.text.toString().trim()

            // E-posta ve şifrenin boş olup olmadığını kontrol et
            if (email.isEmpty()) {
                Toast.makeText(context, "Lütfen e-posta adresinizi girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(context, "Lütfen şifrenizi girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                Toast.makeText(context, "Lütfen şifrenizi tekrar girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Şifrelerin eşleşip eşleşmediğini kontrol et
            if (password != confirmPassword) {
                Toast.makeText(context, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Eğer kontroller geçilirse kullanıcıyı kaydet
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Kayıt başarılı
                        Toast.makeText(context, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                        // Anasayfaya yönlendirme veya başka bir işlem yapabilirsiniz
                    } else {
                        // Kayıt başarısız
                        Toast.makeText(context, "Kayıt Başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
