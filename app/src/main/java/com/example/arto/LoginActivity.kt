package com.example.arto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inisialisasi view dari layout
        etNama = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Inisialisasi Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this)

        // Penyesuaian untuk layout utama (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cardLogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Event klik tombol login
        btnLogin.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty()) {
                etNama.error = "Nama tidak boleh kosong"
                etNama.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password tidak boleh kosong"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            loginUser(nama, password)
        }
    }

    private fun loginUser(nama: String, password: String) {
        val url = Db_kontrak.urlLogin

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.optInt("success", 0)

                    if (success == 1) {
                        val user = jsonObject.getJSONObject("user")
                        val namaUser = user.getString("nama")

                        // Simpan session
                        val sharedPref = getSharedPreferences("USER_DATA", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("nama", namaUser)
                            apply()
                        }

                        Toast.makeText(this, "Login berhasil! Selamat datang, $namaUser", Toast.LENGTH_SHORT).show()

                        // Pindah ke halaman utama
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, "Nama atau password salah!", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Kesalahan saat membaca data!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "nama" to nama,
                    "password" to password
                )
            }
        }

        requestQueue.add(stringRequest)
    }
}
