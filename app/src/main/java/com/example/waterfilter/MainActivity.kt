package com.example.waterfilter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.waterfilter.Login.LoginRequest
import com.example.waterfilter.Login.LoginResponse
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

        if (isLoggedIn) {
            // User is already logged in, navigate to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()  // Close the current activity
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

    private fun login(phone: String, password: String) {
        val apiService = ApiClient.getClient("http://rgd.amusoft.uz/api/").create(ApiService::class.java)
        val loginRequest = LoginRequest(phone, password)
        val call = apiService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when (response.code()) {
                    200 -> {
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
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()  // Optional: Close the login activity
                        }
                    }
                    422 -> {
                        Toast.makeText(this@MainActivity, "Login failed: Invalid account information", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
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
