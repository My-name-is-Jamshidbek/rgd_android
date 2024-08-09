package com.example.waterfilter.data.Login

data class LoginRequest(
        val phone: String,
        val password: String,
        val role: String = "dealer"
)
