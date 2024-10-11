package com.example.testcraft

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.testcraft.databinding.ActivityHomePageBinding
import com.example.testcraft.loginandsignup.LoginSignupPageActivity
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomdialoghelper: AddQuestionDialogHelper
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("BindingCheck", "binding: ${binding.navigationView}")

        replaceFragment(TestPageFragment())

        setupDrawer()

        setSupportActionBar(binding.toolbar) //ekledin

        bottomdialoghelper = AddQuestionDialogHelper(this)

        // Firebase Authentication başlat
        auth = FirebaseAuth.getInstance()

        // NavigationView başlığını şişir ve TextView'i bul
        val headerView: View = binding.navigationView.getHeaderView(0)
        val navHeaderEmailTextView: TextView = headerView.findViewById(R.id.nav_header_email)

        // Giriş yapan kullanıcının e-posta adresini almak ve TextView'e set etmek
        val currentUser = auth.currentUser
        navHeaderEmailTextView.text = currentUser?.email ?: "E-posta bulunamadı"

        binding.apply {
            navigationView.setNavigationItemSelectedListener { menuItem ->
                Log.d("navigationCheck", "navView: ${binding.navigationView}")

                when (menuItem.itemId) {
                    R.id.profile -> {
                        replaceFragment(ProfilePageFragment())
                        showToast("Profile gir")
                    }
                    R.id.settings -> {
                        replaceFragment(SettingsPageFragment())
                        showToast("Ayarlara gir")
                    }
                    R.id.logout -> {
                        showToast("Çıkış yapıldı")

                        // Login sayfasına yönlendirme
                        val intent = Intent(
                            this@HomePageActivity,
                            LoginSignupPageActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Bu aktiviteyi kapatır
                    }
                    else -> {
                        Log.d("tıklamacheck", "navView: ${binding.navigationView}")
                    }
                }
                true
            }

            bottomNavigationView.setOnItemSelectedListener { menuItem ->
                Log.d("bottomChek", "bottom: ${binding.bottomNavigationView}")

                when (menuItem.itemId) {
                    R.id.test -> replaceFragment(TestPageFragment())
                    R.id.archive -> replaceFragment(ArchivePageFragment())
                    else -> {
                    }
                }
                true
            }

            binding.fab.setOnClickListener {
                bottomdialoghelper.showBottomDialog()
            }
        }
    }

    // Yan menü çizgisini ayarlıyor
    private fun setupDrawer() {
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this@HomePageActivity,
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        Log.d("ToggleCheck", "Toggle: $toggle")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("HomePage", "onOptionsItemSelected called: ${item.itemId}")

        // ActionBarDrawerToggle için özel işlem
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bottomdialoghelper.photoHandler.handleActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PhotoHandler.REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bottomdialoghelper.photoHandler.openCamera()
                } else {
                    Toast.makeText(this, "Kamera izni verilmedi", Toast.LENGTH_SHORT).show()
                }
            }
            PhotoHandler.REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bottomdialoghelper.photoHandler.openGallery()
                } else {
                    Toast.makeText(this, "Galeriden fotoğraf yükleme izni verilmedi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}
