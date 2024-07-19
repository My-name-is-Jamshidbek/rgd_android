package com.example.waterfilter;
import com.example.waterfilter.Login.LoginRequest;
import com.example.waterfilter.Login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
