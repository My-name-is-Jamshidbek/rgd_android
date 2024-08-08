package com.example.waterfilter.activities.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waterfilter.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AgentHistoryActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_history)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.nav_view)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Perform action for Home
                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AgentTaskListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_history -> {
                    // Perform action for History
                    Toast.makeText(this, "History selected", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AgentHistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_products -> {
                    // Perform action for Products
                    Toast.makeText(this, "Products selected", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AgentProductsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    // Perform action for Profile
                    Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
