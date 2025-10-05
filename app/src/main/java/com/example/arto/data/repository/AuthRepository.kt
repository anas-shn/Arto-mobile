package com.example.arto.data.repository

import android.content.Context
import android.util.Log
import com.example.arto.data.local.SessionManager
import com.example.arto.data.model.AuthResponse
import com.example.arto.data.model.LoginRequest
import com.example.arto.data.model.RegisterRequest
import com.example.arto.data.model.UserData
import com.example.arto.data.network.InstanceRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {

    private val apiService = InstanceRetrofit.api
    private val sessionManager = SessionManager(context)

    companion object {
        private const val TAG = "AuthRepository"
    }

    // Login
    suspend fun login(email: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "=== LOGIN REQUEST ===")
                Log.d(TAG, "Email: $email")

                val request = LoginRequest(email, password)
                val response = apiService.login(request)

                Log.d(TAG, "Response code: ${response.code()}")

                when {
                    response.isSuccessful -> {
                        // Ambil user pertama dari array
                        val userList = response.body()
                        if (!userList.isNullOrEmpty()) {
                            val user = userList[0]

                            // Generate fake token (karena API tidak return token)
                            val fakeToken = "token_${user.id}_${System.currentTimeMillis()}"

                            // Save session
                            sessionManager.saveAuthToken(fakeToken)
                            sessionManager.saveUserData(user.id, user.name, user.email)

                            // Convert to AuthResponse
                            val authResponse = AuthResponse(
                                success = true,
                                message = "Login berhasil",
                                data = user,
                                token = fakeToken
                            )

                            Log.d(TAG, "Session saved successfully")
                            Log.d(TAG, "=== LOGIN SUCCESS ===")
                            Result.success(authResponse)
                        } else {
                            Log.e(TAG, "User list is empty")
                            Result.failure(Exception("Email atau password salah"))
                        }
                    }
                    response.code() == 401 -> {
                        Log.e(TAG, "Unauthorized - 401")
                        Result.failure(Exception("Email atau password salah"))
                    }
                    response.code() == 404 -> {
                        Log.e(TAG, "User not found - 404")
                        Result.failure(Exception("User tidak ditemukan"))
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Login failed: ${response.code()} - $errorBody")
                        Result.failure(Exception("Login gagal: ${response.message()}"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login exception", e)
                Log.e(TAG, "=== LOGIN EXCEPTION ===")
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }

    // Register
    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== REGISTER REQUEST ===")
            Log.d(TAG, "Name: $name, Email: $email")

            val request = RegisterRequest(name, email, password, passwordConfirmation)
            val response = apiService.register(request)

            Log.d(TAG, "Response code: ${response.code()}")

            when {
                response.isSuccessful -> {
                    val authResponse = response.body()
                    Log.d(TAG, "Response body: $authResponse")

                    if (authResponse != null && authResponse.success) {
                        Log.d(TAG, "=== REGISTER SUCCESS ===")
                        Result.success(authResponse)
                    } else {
                        val error = authResponse?.message ?: "Register failed"
                        Log.e(TAG, "Register failed: $error")
                        Result.failure(Exception(error))
                    }
                }
                response.code() == 409 -> {
                    Log.e(TAG, "Conflict - Email already exists")
                    Result.failure(Exception("Email sudah terdaftar"))
                }
                else -> {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Register failed: ${response.code()} - $errorBody")
                    Result.failure(Exception("Registrasi gagal: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Register exception", e)
            Log.e(TAG, "=== REGISTER EXCEPTION ===")
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Logout
    fun logout() {
        sessionManager.clearSession()
        Log.d(TAG, "User logged out, session cleared")
    }

    // Check if logged in
    fun isLoggedIn(): Boolean {
        val loggedIn = sessionManager.isLoggedIn()
        Log.d(TAG, "isLoggedIn: $loggedIn")
        return loggedIn
    }
}