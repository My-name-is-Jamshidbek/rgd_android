package com.example.waterfilter.api.Login

data class LoginResponse(
    val message: String,
    val data: Data
) {
    data class Data(
        val user: User,
        val token: String
    ) {
        data class User(
            val id: Int,
            val name: String,
            val phone: Int,
            val createdAt: String,
            val updatedAt: String
        )
    }
}
