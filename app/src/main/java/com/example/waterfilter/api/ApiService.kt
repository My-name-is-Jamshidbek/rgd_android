package com.example.waterfilter.api

import com.example.waterfilter.data.setLocation.LocationData
import com.example.waterfilter.data.Login.LoginRequest
import com.example.waterfilter.data.Login.LoginResponse
import com.example.waterfilter.data.*
import com.example.waterfilter.data.common.JsonResponse
import com.example.waterfilter.data.getTaskById.TaskResponse
import com.example.waterfilter.data.getTasks.TaskListResponse
import com.example.waterfilter.data.pointLocation.SetPointLocationRequest
import com.example.waterfilter.data.setTaskProducts.ProductRequest
import com.example.waterfilter.data.updateProfile.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

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

    @POST("task/{id}/complete")
    fun setTaskProducts(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body products: ProductRequest
    ): Call<Void>

    @PUT("point/location")
    fun setPointLocation(@Header("Authorization") token: String, @Body request: SetPointLocationRequest): Call<Void>

    @POST("task/{id}/verify")
    fun verifySmsCode(
        @Header("Authorization") token: String,
        @Path("id") taskId: String,
        @Body request: Map<String, Int>
    ): Call<JsonResponse>

}
