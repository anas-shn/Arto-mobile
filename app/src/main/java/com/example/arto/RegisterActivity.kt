package com.example.arto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etKonfirmasiPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Inisialisasi komponen dari layout
        etNama = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        // Inisialisasi Volley
        requestQueue = Volley.newRequestQueue(this)

        // Penyesuaian padding untuk status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Event klik untuk link "Sudah punya akun? Login!"
        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Aksi tombol register
        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val konfirmasiPassword = etKonfirmasiPassword.text.toString().trim()

            // Validasi input
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

            if (konfirmasiPassword.isEmpty()) {
                etKonfirmasiPassword.error = "Konfirmasi password tidak boleh kosong"
                etKonfirmasiPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != konfirmasiPassword) {
                etKonfirmasiPassword.error = "Konfirmasi password tidak cocok!"
                etKonfirmasiPassword.requestFocus()
                return@setOnClickListener
            }

            // Jalankan proses registrasi
            registerUser(nama, password)
        }
    }

    private fun registerUser(nama: String, password: String) {
        val url = Db_kontrak.urlRegister

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.optInt("success", 0)

                    if (success == 1) {
                        Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                        // Setelah berhasil, langsung ke halaman login
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Registrasi gagal, nama sudah digunakan.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Terjadi kesalahan saat memproses data!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Gagal terhubung ke server!", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nama"] = nama
                params["password"] = password
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}
