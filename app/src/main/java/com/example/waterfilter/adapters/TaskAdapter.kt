package com.example.waterfilter.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.activities.TaskActivity
import com.example.waterfilter.data.SetPointLocationRequest
import com.example.waterfilter.data.TaskResponse
import com.example.waterfilter.api.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskAdapter(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val apiService: ApiService,
    private val taskId: Int
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var taskResponse: TaskResponse? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val locationSetLayout: LinearLayout = view.findViewById(R.id.locationSetLayout)
        val locationSetAboutTextView: TextView = view.findViewById(R.id.setLoctionAboutTextView)
        val setLoctionTextView: TextView = view.findViewById(R.id.setLoctionTextView)
        val setLoctionButton: Button = view.findViewById(R.id.setLoctionButton)

        val clientNameTextView: TextView = view.findViewById(R.id.clientNameTextView)
        val clientPhoneTextView: TextView = view.findViewById(R.id.clientPhoneTextView)
        val clientDescTextView: TextView = view.findViewById(R.id.clientDescTextView)

        val pointLocationTextView: Button = view.findViewById(R.id.pointLocationTextView)
        val pointModelTextView: TextView = view.findViewById(R.id.pointModelTextView)
        val pointExpireDateTextView: TextView = view.findViewById(R.id.pointExpireDateTextView)
        val pointExpireTextView: TextView = view.findViewById(R.id.pointExpireTextView)
        val pointInstallationDateTextView: TextView = view.findViewById(R.id.pointInstallationDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        taskResponse?.let { response ->
            val task = response.task
            val client = task.client
            val point = task.point

            if (point.latitude != null && point.longitude != null) {
                holder.locationSetLayout.visibility = View.GONE
            }

            holder.locationSetAboutTextView.text = context.getString(R.string.set_laction_about_text)
            holder.setLoctionTextView.text = context.getString(R.string.set_laction_text)
            holder.setLoctionButton.text = context.getString(R.string.set_location_btn)

            holder.clientNameTextView.text = client.name
            holder.clientPhoneTextView.text = client.phone
            holder.clientDescTextView.text = client.description

            holder.pointLocationTextView.text = context.getString(R.string.poin_location_btn)
            holder.pointModelTextView.text = point.filterId.toString()
            holder.pointExpireDateTextView.text = point.filterExpireDate
            holder.pointExpireTextView.text = point.filterExpire.toString()
            holder.pointInstallationDateTextView.text = point.installationDate

            holder.setLoctionButton.setOnClickListener {
                (context as TaskActivity).requestLocationPermission()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTaskResponse(taskResponse: TaskResponse) {
        this.taskResponse = taskResponse
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyLocationSent() {
        taskResponse?.task?.point?.latitude = null
        taskResponse?.task?.point?.longitude = null
        notifyDataSetChanged()
    }

    fun sendCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "") ?: return@addOnSuccessListener
                    val request = SetPointLocationRequest(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        point_id = taskId // Assuming the task ID is the same as point ID
                    )
                    apiService.setPointLocation("Bearer $token", request).enqueue(object : Callback<Void> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Location sent", Toast.LENGTH_SHORT).show()
                                notifyLocationSent()
                            } else {
                                Toast.makeText(context, "Qandaydir muammo yuz berdi", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(context, "Qandaydir muammo yuz berdi", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (taskResponse != null) 1 else 0
    }
}
