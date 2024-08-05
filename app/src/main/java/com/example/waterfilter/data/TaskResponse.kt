package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName


data class TaskResponse(
    @SerializedName("agent_products") val agentProducts: List<AgentProduct>,
    @SerializedName("task") val task: Task
)