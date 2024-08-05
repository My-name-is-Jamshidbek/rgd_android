package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName


data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: Int
)