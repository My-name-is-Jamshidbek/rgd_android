package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName
import java.util.Date

data class AgentProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("agent_id") val agentId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Int,
    @SerializedName("service_price") val servicePrice: Int,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date,
    @SerializedName("product") val product: Product
)