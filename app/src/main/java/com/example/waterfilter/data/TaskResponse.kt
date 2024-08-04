package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("tasks") val tasks: List<Task>
)