package com.example.waterfilter.data.getDemos
import com.example.waterfilter.data.common.Demo
import com.google.gson.annotations.SerializedName

data class DemosListResponse(
    @SerializedName("demos") val demos: List<Demo>
)