package com.example.waterfilter.api

import com.example.waterfilter.api.Login.LoginRequest
import com.example.waterfilter.api.Login.LoginResponse
import com.example.waterfilter.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("login/")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @PUT("updateProfile")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<User>
}
