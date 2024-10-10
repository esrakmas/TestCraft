package com.example.testcraft.loginandsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import android.os.Bundle
import android.view.View
import com.example.testcraft.HomePageActivity
import com.example.testcraft.databinding.ActivityLoginSignupPageBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp


class LoginSignupPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginSignupPageBinding
    private lateinit var adapter: LoginSignupPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Firebase’i başlat
        binding = ActivityLoginSignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // View Binding ile TabLayout ve ViewPager2'e erişim
        val tabLayout: TabLayout = binding.tabLayout
        val viewPager2: ViewPager2 = binding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("Signup"))

        val fragmentManager: FragmentManager = supportFragmentManager
        adapter = LoginSignupPageAdapter(fragmentManager, lifecycle)
        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager2.currentItem = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No action needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No action needed
            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }
        })

    }



}