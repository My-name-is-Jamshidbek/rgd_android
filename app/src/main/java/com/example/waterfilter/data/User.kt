package com.example.waterfilter.data

data class User(
    val name: String,
    val phone: String,
    val current_password: String?,
    val new_password: String?,
    val new_password_confirmation: String?
)
