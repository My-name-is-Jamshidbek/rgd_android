package com.example.waterfilter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.waterfilter.Login.LoginRequest
import com.example.waterfilter.Login.LoginResponse
import com.example.waterfilter.Login.TokenValidationResponse
import com.example.waterfilter.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val token = sharedPreferences.getString("token", "N/A")

        if (isLoggedIn) {
            // Check if the token is still valid
            validateToken(token ?: "")
        } else {
            // User is not logged in, show the login screen
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.loginButton.setOnClickListener {
                val phone = binding.phone.text.toString()
                val password = binding.password.text.toString()
                login(phone, password)
            }
        }
    }

    private fun validateToken(token: String) {
        val apiService = ApiClient.getClient("http://rgd.amusoft.uz/api/").create(ApiService::class.java)
        val call = apiService.validateToken("Bearer $token")

        call.enqueue(object : Callback<TokenValidationResponse> {
            override fun onResponse(call: Call<TokenValidationResponse>, response: Response<TokenValidationResponse>) {
                if (response.isSuccessful && response.body()?.isValid == true) {
                    // Token is valid, navigate to HomeActivity
                    val intent = Intent(this@MainActivity, WebViewActivity::class.java)
                    intent.putExtra("TOKEN", token)
                    startActivity(intent)
                    finish()  // Optional: Close the login activity
                } else {
                    // Token is invalid, clear shared preferences and show login screen
                    val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    binding = ActivityMainBinding.inflate(layoutInflater)
                    setContentView(binding.root)

                    binding.loginButton.setOnClickListener {
                        val phone = binding.phone.text.toString()
                        val password = binding.password.toString()
                        login(phone, password)
                    }
                }
            }

            override fun onFailure(call: Call<TokenValidationResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Token validation failed: ${t.message}", Toast.LENGTH_SHORT).show()
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                binding.loginButton.setOnClickListener {
                    val phone = binding.phone.text.toString()
                    val password = binding.password.text.toString()
                    login(phone, password)
                }
            }
        })
    }

    private fun login(phone: String, password: String) {
        val apiService = ApiClient.getClient("http://rgd.amusoft.uz/api/").create(ApiService::class.java)
        val loginRequest = LoginRequest(phone, password)
        val call = apiService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when (response.code()) {
                    200 -> {
                        Log.e("200", "Login failed: ${response.errorBody()?.string()}")
                        response.body()?.let { loginResponse ->
                            val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.putInt("userId", loginResponse.data.user.id)
                            editor.putString("userName", loginResponse.data.user.name)
                            editor.putString("userPhone", loginResponse.data.user.phone.toString())
                            editor.putString("token", loginResponse.data.token)
                            editor.apply()

                            Toast.makeText(
                                this@MainActivity,
                                "Login successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@MainActivity, WebViewActivity::class.java)
                            intent.putExtra("TOKEN", loginResponse.data.token)
                            startActivity(intent)
                            finish()  // Optional: Close the login activity
                        }
                    }
                    422 -> {
                        Log.e("LoginError", "Login failed: ${response.errorBody()?.string()}")
                        Toast.makeText(this@MainActivity, "Login failed: Invalid account information", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.e("LoginError", "Login failed: ${response.errorBody()?.string()}")
                        Toast.makeText(this@MainActivity, "Login failed: Unexpected error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Login Failed: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
