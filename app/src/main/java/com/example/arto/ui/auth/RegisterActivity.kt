package com.example.arto.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.arto.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    
    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "=== RegisterActivity onCreate ===")
        
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        // Loading state
        authViewModel.isLoading.observe(this) { isLoading ->
            Log.d(TAG, "Loading state: $isLoading")
            if (isLoading) showLoading() else hideLoading()
        }

        // Register success
        authViewModel.registerSuccess.observe(this) { success ->
            Log.d(TAG, "Register success observed: $success")
            if (success) {
                Toast.makeText(this, "Registrasi berhasil! Silakan login", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
        }

        // Error
        authViewModel.error.observe(this) { error ->
            error?.let {
                Log.e(TAG, "Register error: $it")
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                authViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {
        // Register button
        binding.btnRegister.setOnClickListener {
            val name = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etKonfirmasiPassword.text.toString().trim()
            
            Log.d(TAG, "Register button clicked")
            Log.d(TAG, "Name: $name")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Password length: ${password.length}")

            authViewModel.register(name, email, password, confirmPassword)
        }

        // Login link
        binding.tvLoginLink.setOnClickListener {
            Log.d(TAG, "Navigating to LoginActivity")
            navigateToLogin()
        }
    }

    private fun showLoading() {
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Loading..."
        binding.etNama.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.etKonfirmasiPassword.isEnabled = false
    }

    private fun hideLoading() {
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = "Registrasi"
        binding.etNama.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.etKonfirmasiPassword.isEnabled = true
    }

    private fun navigateToLogin() {
        Log.d(TAG, "=== Navigating to LoginActivity ===")
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}