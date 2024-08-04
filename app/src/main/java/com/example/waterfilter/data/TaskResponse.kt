package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("all_services") val allServices: List<Service>,
    @SerializedName("task") val task: Task
)