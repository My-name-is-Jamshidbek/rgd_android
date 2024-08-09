package com.example.waterfilter.data.common
import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("description") val description: String?,
)