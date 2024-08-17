package com.example.testcraft

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.example.testcraft.databinding.ActivityHomePageBinding
import com.google.firebase.firestore.FirebaseFirestore

class  HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomdialoghelper: BottomDialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("BindingCheck", "binding: ${binding.navigationView}")

        replaceFragment(TestFragment())

        setupDrawer()

        setSupportActionBar(binding.toolbar) //ekledin

        bottomdialoghelper = BottomDialogHelper(this)



        binding.apply {


            navigationView.setNavigationItemSelectedListener() {menuItem ->

                Log.d("navigationCheck", "navView: ${binding.navigationView}")

                when (menuItem.itemId) {
                    R.id.profile -> replaceFragment(ProfileFragment())
                    R.id.settings -> showToast("Ayarlara gir")
                    R.id.logout -> {
                        showToast("Çıkış yapıldı")

                        // Login sayfasına yönlendirme
                        val intent = Intent(this@HomePage, LoginSignUpPage::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Bu aktiviteyi kapatır
                    }
                    else->{
                        Log.d("tıklamacheck", "navView: ${binding.navigationView}")
                    }
                }
               // binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }


            bottomNavigationView.setOnItemSelectedListener {menuItem ->
                    Log.d("bottomChek", "bottom: ${binding.bottomNavigationView}")

                    when (menuItem.itemId) {
                    R.id.test -> replaceFragment(TestFragment())
                    R.id.archive -> replaceFragment(ArchiveFragment())
                    else->{
                    }
                }
                true
            }


            binding.fab.setOnClickListener {
                bottomdialoghelper.showBottomDialog()
            }


        }

    }

    //yan menü çizgisini ayarlıyor
    private fun setupDrawer() {
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this@HomePage, binding.drawerLayout, R.string.open, R.string.close) //Bu, yan menü simgesini (hamburger menüsü) ve metin kaynaklarını tanımlar.
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        Log.d("ToggleCheck", "Toggle: $toggle")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_layout,fragment)
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





}
