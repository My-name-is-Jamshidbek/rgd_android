package com.example.waterfilter.data.getTasks
import com.example.waterfilter.data.common.Task
import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("tasks") val tasks: List<Task>
)