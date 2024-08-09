package com.example.waterfilter.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.waterfilter.fragments.DealerHistoryFragment
import com.example.waterfilter.fragments.DealerDemosListFragment
import com.example.waterfilter.services.LocationService
import com.example.waterfilter.R
import com.example.waterfilter.activities.pages.LoginActivity
import com.example.waterfilter.fragments.AgentProductsFragment
import com.example.waterfilter.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val LocationCode = 1000
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var bottomNavigationView: BottomNavigationView

    private var locationRunnable: Runnable = Runnable { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.nav_view)
        setupBottomNavigation()

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(DealerDemosListFragment())
        }

        checkPermissions()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(DealerDemosListFragment())
                    true
                }
                R.id.navigation_history -> {
                    loadFragment(DealerHistoryFragment())
                    true
                }
                R.id.navigation_products -> {
                    loadFragment(AgentProductsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LocationCode)
        } else {
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                (grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                startLocationService()
            } else {
                Toast.makeText(this, "Lokatsiyani uzatishga ruxsat etilmagan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                loadFragment(SettingsFragment())
                true
            }
            R.id.logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        Toast.makeText(this, "Hisobdan chiqildi", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(locationRunnable)
    }
}
