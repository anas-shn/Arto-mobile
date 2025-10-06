package com.example.arto.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.arto.MainActivity
import com.example.arto.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "=== LoginActivity onCreate ===")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Check if already logged in
        if (authViewModel.isLoggedIn()) {
            Log.d(TAG, "User already logged in, navigating to MainActivity")
            navigateToMain()
            return
        }

        Log.d(TAG, "User not logged in, showing login form")
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        // Loading state
        authViewModel.isLoading.observe(this) { isLoading ->
            Log.d(TAG, "isLoading observed: $isLoading")
            if (isLoading) showLoading() else hideLoading()
        }

        // Login success - PERBAIKAN DI SINI
        authViewModel.loginSuccess.observe(this) { success ->
            Log.d(TAG, "loginSuccess observed: $success")
            if (success == true) {
                Log.d(TAG, "Success is TRUE, showing toast and navigating")
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                // Delay sedikit untuk memastikan Toast muncul
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.d(TAG, "Calling navigateToMain() after delay")
                    navigateToMain()
                }, 500) // 500ms delay
            } else {
                Log.d(TAG, "Success is FALSE or NULL")
            }
        }

        // Error
        authViewModel.error.observe(this) { error ->
            error?.let {
                Log.e(TAG, "Login error: $it")
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                authViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {
        // Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            Log.d(TAG, "=== Login button clicked ===")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Password length: ${password.length}")

            authViewModel.login(email, password)
        }

        // Register link
        binding.tvregisterlink.setOnClickListener {
            Log.d(TAG, "Navigating to RegisterActivity")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showLoading() {
        Log.d(TAG, "showLoading()")
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Loading..."
        binding.etEmail.isEnabled = false
        binding.etPassword.isEnabled = false
    }

    private fun hideLoading() {
        Log.d(TAG, "hideLoading()")
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = "LOGIN"
        binding.etEmail.isEnabled = true
        binding.etPassword.isEnabled = true
    }

    private fun navigateToMain() {
        Log.d(TAG, "=== navigateToMain() called ===")
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.d(TAG, "Starting MainActivity with intent")
            startActivity(intent)
            Log.d(TAG, "Finishing LoginActivity")
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to MainActivity", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}