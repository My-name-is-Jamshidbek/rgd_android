package com.example.waterfilter.api

import android.content.Context
import com.example.waterfilter.R
import okhttp3.OkHttpClient
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
                val interceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                }

                // Build OkHttpClient with the interceptor
                val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()

                // Build and return Retrofit instance
                return Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
        }

        // Method to get ApiService instance
        fun getApiService(context: Context): ApiService {
                val baseUrl = context.getString(R.string.api_base_url)
                return getClient(baseUrl).create(ApiService::class.java)
        }
}
