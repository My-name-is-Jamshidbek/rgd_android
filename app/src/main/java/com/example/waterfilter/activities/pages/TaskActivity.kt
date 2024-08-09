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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.waterfilter.R
import com.example.waterfilter.adapters.ProductAdapter
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.common.AgentProduct
import com.example.waterfilter.data.pointLocation.SetPointLocationRequest
import com.example.waterfilter.data.setTaskProducts.ProductRequest
import com.example.waterfilter.data.common.TaskProduct
import com.example.waterfilter.data.getTaskById.TaskResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    private lateinit var pointModelTextView: TextView
    private lateinit var pointExpireDateTextView: TextView
    private lateinit var pointExpireTextView: TextView
    private lateinit var pointInstallationDateTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var addProductButton: Button
    private lateinit var productAdapter: ProductAdapter
    private var taskProducts: MutableList<AgentProduct> = mutableListOf() // Initialize with an empty list
    private lateinit var agentProduct: AgentProduct
    private var agentProducts: List<AgentProduct> = emptyList() // Initialize with an empty list


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_task)

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

        pointModelTextView = findViewById(R.id.pointModelTextView)
        pointExpireDateTextView = findViewById(R.id.pointExpireDateTextView)
        pointExpireTextView = findViewById(R.id.pointExpireTextView)
        pointInstallationDateTextView = findViewById(R.id.pointInstallationDateTextView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)


        // Initialize RecyclerView for products
        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.setHasFixedSize(true)

        // Initialize add product button and set click listener
        addProductButton = findViewById(R.id.addProduct)
        addProductButton.setOnClickListener {
            // Create a new product item
//            Toast.makeText(this@TaskActivity, "Product added", Toast.LENGTH_SHORT).show()
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
        swipeRefreshLayout.setOnRefreshListener {
            fetchTaskDetails(taskId)
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
//                        Toast.makeText(this@TaskActivity, "Task products sent successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@TaskActivity, SmsVerificationActivity::class.java)
                        intent.putExtra("TASK_ID", taskId)
                        intent.putExtra("PHONE", clientPhoneTextView.text)
                        startActivity(intent)
                    } else if (response.code() == 401) {
                        // Handle unauthorized access (401)
                        Toast.makeText(this@TaskActivity, "Siz tizimga kirmagansiz. Iltimos, qayta kiring.", Toast.LENGTH_SHORT).show()

                        // Clear shared preferences or any stored user session data
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()

                        // Redirect the user to the login activity
                        val intent = Intent(this@TaskActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@TaskActivity, "Qandaydir xatolik yuz berdi iltimos qayta urinib ko`ring!", Toast.LENGTH_SHORT).show()
                        Log.e(ContentValues.TAG, " to Failedsend task products: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(ContentValues.TAG, "Error sending task products", e)
                    Toast.makeText(this@TaskActivity, "Iltimos internet aloqasini tekshiring!", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@TaskActivity, "Qandaydir xatolik yuz berdi iltimos qayta urinib ko`ring!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Iltimos internet aloqasini tekshiring!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
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

        pointModelTextView.text = point.filterId.toString()
        pointExpireDateTextView.text = point.filterExpireDate.substring(0, 10)
        pointExpireTextView.text = "${point.filterExpire.toString()} oy"
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
                                Toast.makeText(this@TaskActivity, "Lokatsiya ulashildi", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@TaskActivity, "Lokatsiyani uzatishga ruxsat etilmagan!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
