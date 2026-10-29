package com.example.lksandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import com.example.lksandroid.network.*
import com.example.lksandroid.network.ApiClient
import com.example.lksandroid.network.AuthResponse
import com.example.lksandroid.network.AuthDTO
import com.example.lksandroid.network.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
        }
            val request = AuthDTO(email = email, password = password)

            ApiClient.instance.login(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()?.data

                        val sharedPrefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        sharedPrefs.edit().apply {
                            putString("USER_NAME", user?.fullname)
                            putString("USER_EMAIL", user?.email)
                            putString("USER_PHONE", user?.phoneNumber ?: "081234567890")
                            apply()
                        }

                        if (user?.role == "JobSeeker") {
                            Toast.makeText(
                                this@MainActivity,
                                "Login berhasil! selamat datang, ${user?.fullname}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@MainActivity, JobListActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Akses ditolak! hanya JobSeeker yang boleh masuk.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        } else {
                            Toast.makeText(
                                this@MainActivity, "Login gagal! Email atau Password salah",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        }

                override fun onFailure(call: Call<AuthResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity, "Gagal terhubung ke sever! Periksa koneksi anda.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                        })
                    }
        tvRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
                }
            }