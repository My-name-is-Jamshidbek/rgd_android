package com.example.waterfilter.api

import com.example.waterfilter.api.Login.LoginRequest
import com.example.waterfilter.api.Login.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login/")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
