package com.example.waterfilter.api

import android.content.Context
import com.example.waterfilter.R
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

        @Volatile
        private var retrofit: Retrofit? = null

        // Method to get Retrofit client instance
        fun getClient(baseUrl: String): Retrofit {
                return retrofit ?: synchronized(this) {
                        retrofit ?: buildRetrofit(baseUrl).also { retrofit = it }
                }
        }

        // Method to build Retrofit instance
        private fun buildRetrofit(baseUrl: String): Retrofit {
                // Create an interceptor for logging HTTP request and response data
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                }

                // Create an interceptor to add headers
                val headerInterceptor = Interceptor { chain ->
                        val original = chain.request()
                        val requestBuilder: Request.Builder = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                        val request: Request = requestBuilder.build()
                        chain.proceed(request)
                }

                // Build OkHttpClient with the interceptors
                val client = OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(headerInterceptor)
                        .build()

                // Configure Gson to be lenient
                val gson = GsonBuilder()
                        .setLenient()
                        .create()

                // Build and return Retrofit instance
                return Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
        }

        // Method to get ApiService instance
        fun getApiService(context: Context): ApiService {
                val baseUrl = context.getString(R.string.api_base_url)
                return getClient(baseUrl).create(ApiService::class.java)
        }
}
