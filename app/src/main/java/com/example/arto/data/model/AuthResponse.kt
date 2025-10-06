package com.example.arto.data.model

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

// Register Request
data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

// Auth Response (Format 1 - dengan success/message/token)
data class AuthResponse(
    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: UserData? = null,

    @SerializedName("token")
    val token: String? = null
)

// User Data
data class UserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

// Login Response (Format 2 - direct user object atau array)
typealias LoginResponse = UserData

// Register Response (Format 2 - direct user object atau array)
typealias RegisterResponse = UserData