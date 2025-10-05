package com.example.arto.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.arto.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    companion object {
        private const val TAG = "AuthViewModel"
    }

    // Login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "=== LOGIN START ===")
                _isLoading.value = true
                _error.value = null
                _loginSuccess.value = false

                // Validate input
                if (email.isBlank() || password.isBlank()) {
                    _error.value = "Email dan password tidak boleh kosong"
                    _loginSuccess.value = false
                    _isLoading.value = false
                    return@launch
                }

                Log.d(TAG, "Calling authRepository.login()")
                val result = authRepository.login(email, password)

                if (result.isSuccess) {
                    Log.d(TAG, "Login SUCCESS - Setting loginSuccess to true")
                    _loginSuccess.postValue(true)
                    Log.d(TAG, "=== LOGIN SUCCESS ===")
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Login gagal"
                    Log.e(TAG, "Login FAILED: $errorMessage")
                    _error.postValue(errorMessage)
                    _loginSuccess.postValue(false)
                    Log.e(TAG, "=== LOGIN FAILED ===")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Login exception", e)
                _error.postValue("Terjadi kesalahan: ${e.message}")
                _loginSuccess.postValue(false)
            } finally {
                _isLoading.postValue(false)
                Log.d(TAG, "Loading set to false")
            }
        }
    }

    // Register
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "=== REGISTER START ===")
                _isLoading.value = true
                _error.value = null
                _registerSuccess.value = false

                // Validate input
                when {
                    name.isBlank() -> {
                        _error.value = "Nama tidak boleh kosong"
                        _registerSuccess.value = false
                        _isLoading.value = false
                        return@launch
                    }
                    email.isBlank() -> {
                        _error.value = "Email tidak boleh kosong"
                        _registerSuccess.value = false
                        _isLoading.value = false
                        return@launch
                    }
                    password.isBlank() -> {
                        _error.value = "Password tidak boleh kosong"
                        _registerSuccess.value = false
                        _isLoading.value = false
                        return@launch
                    }
                    password != confirmPassword -> {
                        _error.value = "Password tidak cocok"
                        _registerSuccess.value = false
                        _isLoading.value = false
                        return@launch
                    }
                    password.length < 6 -> {
                        _error.value = "Password minimal 6 karakter"
                        _registerSuccess.value = false
                        _isLoading.value = false
                        return@launch
                    }
                }

                val result = authRepository.register(name, email, password, confirmPassword)

                if (result.isSuccess) {
                    Log.d(TAG, "Register SUCCESS")
                    _registerSuccess.postValue(true)
                    Log.d(TAG, "=== REGISTER SUCCESS ===")
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Registrasi gagal"
                    Log.e(TAG, "Register FAILED: $errorMessage")
                    _error.postValue(errorMessage)
                    _registerSuccess.postValue(false)
                    Log.e(TAG, "=== REGISTER FAILED ===")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Register exception", e)
                _error.postValue("Terjadi kesalahan: ${e.message}")
                _registerSuccess.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        val loggedIn = authRepository.isLoggedIn()
        Log.d(TAG, "isLoggedIn check: $loggedIn")
        return loggedIn
    }

    fun clearError() {
        _error.value = null
    }
}