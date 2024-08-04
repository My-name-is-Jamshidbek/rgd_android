package com.example.waterfilter.api

import com.example.locationapp.LocationData
import com.example.waterfilter.api.Login.LoginRequest
import com.example.waterfilter.api.Login.LoginResponse
import com.example.waterfilter.data.SetPointLocationRequest
import com.example.waterfilter.data.TaskListResponse
import com.example.waterfilter.data.TaskResponse
import com.example.waterfilter.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @POST("login/")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @PUT("updateProfile")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<User>


    @POST("set-location")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Body location: LocationData
    ): Response<Void>

    @GET("tasks")
    fun getTasks(
        @Header("Authorization") token: String,
    ): Call<TaskListResponse>

    @GET("tasks/{id}")
    fun getTaskById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<TaskResponse>

    @PUT("point/location")
    fun setPointLocation(@Header("Authorization") token: String, @Body request: SetPointLocationRequest): Call<Void>
}
