package com.example.lksandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class ActivityProfile : AppCompatActivity() {

    private lateinit var ivProfilePicture: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvLabelEmail: TextView
    private lateinit var tvNotelp: TextView
    private lateinit var bottomNavbarLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        bottomNavbarLayout = findViewById(R.id.bottomNavbarLayout)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        tvUsername = findViewById(R.id.tvUsername)
        tvLabelEmail = findViewById(R.id.tvLabelEmail)
        tvNotelp = findViewById(R.id.tvNotelp)

        setupBottomNavbar()
        loadProfileData()
    }

    override fun onResume() {
        super.onResume()
        // Pastikan tab "Profile" (index 2) terpilih saat activity aktif
        bottomNavbarLayout.getTabAt(2)?.select()
    }

    private fun loadProfileData() {
        val sharedPrefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val savedName = sharedPrefs.getString("USER_NAME", null)
        val savedEmail = sharedPrefs.getString("USER_EMAIL", null)
        val savedPhone = sharedPrefs.getString("USER_PHONE", null)

        if (!savedName.isNullOrEmpty()) {
            tvUsername.text = savedName
            tvLabelEmail.text = savedEmail ?: ""
            tvNotelp.text = if (!savedPhone.isNullOrEmpty()) savedPhone else "081234567890"
        } else {
            // Default jika data belum tersimpan
            tvUsername.text = "John Doe"
            tvLabelEmail.text = "johndoe@mail.com"
            tvNotelp.text = "081234567890"
        }
    }

    private fun setupBottomNavbar() {
        bottomNavbarLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // Explore / Job List
                        val intent = Intent(this@ActivityProfile, JobListActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    1 -> { // My Job
                        val intent = Intent(this@ActivityProfile, MyJobActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    2 -> { /* Sudah di Profile */ }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}