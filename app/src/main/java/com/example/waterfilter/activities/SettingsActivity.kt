package com.example.waterfilter.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waterfilter.R
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.User
import com.example.waterfilter.databinding.ActivitySettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var apiService: ApiService

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "N/A")
        val userPhone = sharedPreferences.getString("userPhone", "N/A")

        val userNameEditText: EditText = findViewById(R.id.editFullname)
        val userPhoneEditText: EditText = findViewById(R.id.editPhone)
        val userOldPasswordEditText: EditText = findViewById(R.id.oldPassword)
        val userNewPasswordText: EditText = findViewById(R.id.editNewPassword)
        val userNewPasswordConfirmText: EditText = findViewById(R.id.editNewPasswordConfirm)

        userNameEditText.setText(userName)
        userPhoneEditText.setText(userPhone)

        val toolbar: Toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)

        apiService = ApiClient.getApiService(this)

        val updateButton: Button = findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            val newUserName = userNameEditText.text.toString()
            val newUserPhone = userPhoneEditText.text.toString()
            val oldPassword = userOldPasswordEditText.text.toString()
            val newPassword = userNewPasswordText.text.toString()
            val newPasswordConfirm = userNewPasswordConfirmText.text.toString()
            if (newPassword != newPasswordConfirm) {
                Toast.makeText(this@SettingsActivity, "New password != New password confirmation.", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(newUserName, newUserPhone, oldPassword, newPassword, newPasswordConfirm)
                updateUserData(user, userOldPasswordEditText, userNewPasswordText, userNewPasswordConfirmText)
            }
        }
    }

    private fun updateUserData(user: User, userOldPasswordEditText: EditText, userNewPasswordText: EditText, userNewPasswordConfirmText: EditText) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "") ?: return@launch
                val response = apiService.updateUser("Bearer $token", user)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Save updated user information to SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("userName", user.name)
                        editor.putString("userPhone", user.phone)
                        editor.apply()

                        Toast.makeText(this@SettingsActivity, "User updated successfully", Toast.LENGTH_SHORT).show()
                        // Clear password fields
                        userOldPasswordEditText.text.clear()
                        userNewPasswordText.text.clear()
                        userNewPasswordConfirmText.text.clear()
                    } else {
                        when (response.code()) {
                            401 -> {
                                Toast.makeText(this@SettingsActivity, "You are not logged in. Please log in again.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            404 -> {
                                Toast.makeText(this@SettingsActivity, "Update failed: Resource not found", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val errorMessage = response.errorBody()?.string()
                                Toast.makeText(this@SettingsActivity, "Failed to update user: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                // Navigate back to login screen
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
