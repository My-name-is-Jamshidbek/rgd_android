package com.example.waterfilter.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waterfilter.R
import com.example.waterfilter.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

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
        val userOldPhoneEditText: EditText = findViewById(R.id.editPassword)
        val userNewPasswordText: EditText = findViewById(R.id.editPhone)
        val userNewPasswordConfirmText: EditText = findViewById(R.id.editPhone)

        userNameEditText.setText(userName)
        userPhoneEditText.setText(userPhone)

        val toolbar: Toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)

        val logoutButton: Button = findViewById(R.id.updateButton)
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
                Toast.makeText(this, "Chiqildi", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
