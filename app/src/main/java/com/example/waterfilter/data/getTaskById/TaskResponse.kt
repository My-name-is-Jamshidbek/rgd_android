package com.example.waterfilter.data.getTaskById
import com.example.waterfilter.data.common.AgentProduct
import com.example.waterfilter.data.common.Task
import com.google.gson.annotations.SerializedName


data class TaskResponse(
    @SerializedName("agent_products") val agentProducts: List<AgentProduct>,
    @SerializedName("task") val task: Task
)