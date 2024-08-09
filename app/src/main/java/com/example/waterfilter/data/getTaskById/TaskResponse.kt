package com.example.waterfilter.data.getTaskById
import com.example.waterfilter.data.common.AgentProduct
import com.example.waterfilter.data.common.Demo
import com.google.gson.annotations.SerializedName


data class TaskResponse(
    @SerializedName("agent_products") val agentProducts: List<AgentProduct>,
    @SerializedName("demo") val demo: Demo
)