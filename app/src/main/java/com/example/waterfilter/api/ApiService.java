package com.example.waterfilter.api;
import com.example.waterfilter.api.Login.LoginRequest;
import com.example.waterfilter.api.Login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
