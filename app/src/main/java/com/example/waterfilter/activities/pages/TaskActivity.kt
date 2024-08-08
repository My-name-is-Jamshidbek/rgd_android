package com.example.waterfilter.activities.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.adapters.ProductAdapter
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.AgentProduct
import com.example.waterfilter.data.SetPointLocationRequest
import com.example.waterfilter.data.ProductRequest
import com.example.waterfilter.data.TaskProduct
import com.example.waterfilter.data.TaskResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var taskId: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationSetLayout: LinearLayout
    private lateinit var setLoctionAboutTextView: TextView
    private lateinit var setLoctionTextView: TextView
    private lateinit var setLoctionButton: Button

    private lateinit var clientNameTextView: TextView
    private lateinit var clientPhoneTextView: TextView
    private lateinit var clientDescTextView: TextView

    private lateinit var pointLocationTextView: Button
    private lateinit var pointModelTextView: TextView
    private lateinit var pointExpireDateTextView: TextView
    private lateinit var pointExpireTextView: TextView
    private lateinit var pointInstallationDateTextView: TextView

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var addProductButton: Button
    private lateinit var productAdapter: ProductAdapter
    private var taskProducts: MutableList<AgentProduct> = mutableListOf() // Initialize with an empty list
    private lateinit var agentProduct: AgentProduct
    private var agentProducts: List<AgentProduct> = emptyList() // Initialize with an empty list

    private lateinit var bottomNavigationView: BottomNavigationView

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    @SuppressLint("NotifyDataSetChanged", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // Get task ID from intent
        taskId = intent.getStringExtra("TASK_ID") ?: ""

        // Initialize API service
        apiService = ApiClient.getApiService(this)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize views
        locationSetLayout = findViewById(R.id.locationSetLayout)
        setLoctionAboutTextView = findViewById(R.id.setLoctionAboutTextView)
        setLoctionTextView = findViewById(R.id.setLoctionTextView)
        setLoctionButton = findViewById(R.id.setLoctionButton)

        clientNameTextView = findViewById(R.id.clientNameTextView)
        clientPhoneTextView = findViewById(R.id.clientPhoneTextView)
        clientDescTextView = findViewById(R.id.clientDescTextView)

        pointLocationTextView = findViewById(R.id.pointLocationTextView)
        pointModelTextView = findViewById(R.id.pointModelTextView)
        pointExpireDateTextView = findViewById(R.id.pointExpireDateTextView)
        pointExpireTextView = findViewById(R.id.pointExpireTextView)
        pointInstallationDateTextView = findViewById(R.id.pointInstallationDateTextView)

        // Initialize RecyclerView for products
        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.setHasFixedSize(true)

        // Initialize add product button and set click listener
        addProductButton = findViewById(R.id.addProduct)
        addProductButton.setOnClickListener {
            // Create a new product item
            Toast.makeText(this@TaskActivity, "Product added", Toast.LENGTH_SHORT).show()
            addProduct()
        }

        // Setup a button to send data
        val sendButton: Button = findViewById(R.id.completeButton)
        sendButton.setOnClickListener {
            sendTaskProductsWithCheckboxResults()
        }

        val sendPointLocation: Button = findViewById(R.id.setLoctionButton)

        sendPointLocation.setOnClickListener {
            requestLocationPermission()
        }

        // Fetch task details
        fetchTaskDetails(taskId)
    }


    private fun sendTaskProductsWithCheckboxResults() {
        val selectedProducts = taskProducts.map { agentProduct ->
            TaskProduct(agentProduct.id, agentProduct.isSelected, agentProduct.price, agentProduct.servicePrice)
        }

        val setTaskRequest = ProductRequest(selectedProducts)
        sendToServer(setTaskRequest, taskId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendToServer(productRequest: ProductRequest, taskId: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.setTaskProducts("Bearer $token", taskId, productRequest).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(ContentValues.TAG, "Task products sent successfully")
                        Toast.makeText(this@TaskActivity, "Task products sent successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@TaskActivity, SmsVerificationActivity::class.java)
                        intent.putExtra("TASK_ID", taskId)
                        intent.putExtra("PHONE", clientPhoneTextView.text)
                        startActivity(intent)
                    } else {
                        Log.e(ContentValues.TAG, "Failed to send task products: ${response.code()}")
                        Toast.makeText(this@TaskActivity, "Failed to send task products", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(ContentValues.TAG, "Error sending task products", e)
                    Toast.makeText(this@TaskActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun addProduct() {
        productAdapter.addProduct()
    }

    private fun fetchTaskDetails(taskId: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return
        apiService.getTaskById("Bearer $token", taskId.toInt()).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful) {
                    val taskResponse = response.body()
                    taskResponse?.let {
                        bindData(it)
                    }
                } else {
                    Toast.makeText(this@TaskActivity, "Qandaydir muammo yuz berdi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Qandaydir muammo yuz berdi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bindData(taskResponse: TaskResponse) {
        val task = taskResponse.task
        val client = task.client
        val point = task.point

        this.agentProduct = taskResponse.agentProducts[0]
        this.agentProducts = taskResponse.agentProducts

        if (point.latitude != null && point.longitude != null) {
            locationSetLayout.visibility = View.GONE
        }

        clientNameTextView.text = client.name
        clientPhoneTextView.text = client.phone
        clientDescTextView.text = client.description

        pointLocationTextView.text = getString(R.string.poin_location_btn)
        pointModelTextView.text = point.filterId.toString()
        pointExpireDateTextView.text = point.filterExpireDate
        pointExpireTextView.text = point.filterExpire.toString()
        pointInstallationDateTextView.text = point.installationDate

        // Set up ProductAdapter after fetching the task details
        productAdapter = ProductAdapter(this, taskProducts, agentProducts)
        productRecyclerView.adapter = productAdapter
    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            sendCurrentLocation()
        }
    }

    private fun sendCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "") ?: return@addOnSuccessListener
                    val request = SetPointLocationRequest(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        point_id = taskId.toInt() // Assuming the task ID is the same as point ID
                    )
                    apiService.setPointLocation("Bearer $token", request).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@TaskActivity, "Location sent", Toast.LENGTH_SHORT).show()
                                locationSetLayout.visibility = View.GONE
                            } else {
                                Toast.makeText(this@TaskActivity, "Qandaydir muammo yuz berdi", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@TaskActivity, "Qandaydir muammo yuz berdi", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sendCurrentLocation()
            } else {
                Toast.makeText(this@TaskActivity, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
