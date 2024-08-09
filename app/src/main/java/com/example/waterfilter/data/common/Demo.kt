package com.example.waterfilter.data.common
import com.google.gson.annotations.SerializedName

data class Demo(
    @SerializedName("id") val id: Int,
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("point_id") val pointId: Int,
    @SerializedName("agent_id") val agentId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("is_completed") val isCompleted: Int,
    @SerializedName("service_cost_sum") val serviceCostSum: Int,
    @SerializedName("product_cost_sum") val productCostSum: Int?,
    @SerializedName("cash") val cash: Int,
    @SerializedName("card") val card: Int,
    @SerializedName("terminal") val terminal: Int,
    @SerializedName("transfer") val transfer: Int,
    @SerializedName("service_time") val serviceTime: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("point") val point: Point,
    @SerializedName("client") val client: Client
)
