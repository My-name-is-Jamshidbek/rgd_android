package com.example.waterfilter.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
//import android.os.Build
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.LocationService
import com.example.waterfilter.R
import com.example.waterfilter.adapters.TaskListAdapter
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.TaskListResponse
//import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskListActivity : AppCompatActivity() {

    private val LocationCode = 1000
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView

    private var locationRunnable: Runnable = Runnable { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)

        val toolbar: Toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        apiService = ApiClient.getApiService(this)

        fetchTasks()
        checkPermissions()
    }

    private fun fetchTasks() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return

        apiService.getTasks("Bearer $token").enqueue(object : Callback<TaskListResponse> {
            override fun onResponse(call: Call<TaskListResponse>, response: Response<TaskListResponse>) {
                if (response.isSuccessful) {
                    response.body()?.tasks?.let { tasks ->
                        Toast.makeText(this@TaskListActivity, "Tasks count ${tasks.count()}", Toast.LENGTH_SHORT).show()
                        val taskListAdapter = TaskListAdapter(this@TaskListActivity, tasks)
                        recyclerView.adapter = taskListAdapter
                    } ?: run {
                        Toast.makeText(this@TaskListActivity, "No tasks available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@TaskListActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskListResponse>, t: Throwable) {
                Toast.makeText(this@TaskListActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                Toast.makeText(this, "Location permissions are required for this app", Toast.LENGTH_SHORT).show()
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
                startActivity(Intent(this, SettingsActivity::class.java))
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
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(locationRunnable)
    }
}
