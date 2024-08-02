package com.example.waterfilter;

import com.example.waterfilter.Login.LoginRequest;
import com.example.waterfilter.Login.LoginResponse;
import com.example.waterfilter.Login.TokenValidationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface ApiService {
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("validate-token/")
    Call<TokenValidationResponse> validateToken(@Header("Authorization") String token);
}
