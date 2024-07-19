package com.example.waterfilter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "N/A")
        val userPhone = sharedPreferences.getString("userPhone", "N/A")

        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userPhoneTextView: TextView = findViewById(R.id.userPhoneTextView)

        userNameTextView.text = "Name: $userName"
        userPhoneTextView.text = "Phone: $userPhone"


        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Clear login status
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Navigate back to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
