package com.example.waterfilter.data
import com.google.gson.annotations.SerializedName


data class Region(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)