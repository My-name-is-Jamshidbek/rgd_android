package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id: Int,
    @SerializedName("operator_dealer_id") val operatorDealerId: Int,
    @SerializedName("telegram_id") val telegramId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)