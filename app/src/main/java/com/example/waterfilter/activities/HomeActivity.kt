    package com.example.waterfilter.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.adapters.UserAdapter
import com.example.waterfilter.data.User

class HomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Sample data
        val userList = listOf(
            User("John Doe", "123456789", "123 Street, City", 37.7749, -122.4194),
            User("Jane Smith", "987654321", "456 Avenue, City", 34.0522, -118.2437),
            User("Alice Johnson", "555555555", "789 Boulevard, City", 40.7128, -74.0060)
        )

        val userAdapter = UserAdapter(this, userList)
        recyclerView.adapter = userAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
//        val userName = sharedPreferences.getString("userName", "N/A")
//        val userPhone = sharedPreferences.getString("userPhone", "N/A")
//
//        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
//        val userPhoneTextView: TextView = findViewById(R.id.userPhoneTextView)
//
//        userNameTextView.text = "Name: $userName"
//        userPhoneTextView.text = "Phone: $userPhone"
//
//
//        val logoutButton: Button = findViewById(R.id.logoutButton)
//        logoutButton.setOnClickListener {
//            // Clear login status
//            val editor = sharedPreferences.edit()
//            editor.clear()
//            editor.apply()
//
//            // Navigate back to login screen
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }
}
