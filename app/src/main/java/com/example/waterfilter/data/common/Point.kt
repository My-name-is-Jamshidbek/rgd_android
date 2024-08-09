package com.example.waterfilter.data.common
import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("id") val id: Int,
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("latitude") var latitude: String?,
    @SerializedName("longitude") var longitude: String?,
    @SerializedName("region_id") val regionId: Int,
    @SerializedName("address") val address: String,
    @SerializedName("filter_id") val filterId: Int,
    @SerializedName("filter_expire") val filterExpire: Int,
    @SerializedName("filter_expire_date") val filterExpireDate: String,
    @SerializedName("filter_cost") val filterCost: String?,
    @SerializedName("status") val status: Int,
    @SerializedName("is_full_pay") val isFullPay: Int,
    @SerializedName("invited_client_id") val invitedClientId: String?,
    @SerializedName("contract_date") val contractDate: String?,
    @SerializedName("installation_date") val installationDate: String?,
    @SerializedName("operator_dealer_id") val operatorDealerId: String?,
    @SerializedName("dealer_id") val dealerId: String?,
    @SerializedName("demo_time") val demoTime: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("client") val client: Client
)