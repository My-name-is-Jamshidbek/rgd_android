package com.example.waterfilter.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waterfilter.R
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.api.Login.LoginRequest
import com.example.waterfilter.api.Login.LoginResponse
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
        val apiService = ApiClient.getApiService(this)
        val loginRequest = LoginRequest(phone, password)
        val call = apiService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putInt("userId", loginResponse.data.user.id)
                        editor.putString("userName", loginResponse.data.user.name)
                        editor.putString("userPhone", loginResponse.data.user.phone.toString())
                        editor.putString("token", loginResponse.data.token)
                        editor.apply()

                        Toast.makeText(this@MainActivity, "Kirildi", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()  // Optional: Close the login activity
                    }
                } else {
                    // Log the raw response for debugging
                    val errorBody = response.errorBody()?.string()
                    println("Error: $errorBody")
                    Toast.makeText(this@MainActivity, "Kirishni iloji bo`lmadi: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Login Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
