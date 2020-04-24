package com.adityagupta.mobileclouds.model

import com.google.gson.annotations.SerializedName

data class DeviceModel(
    private val user: String?,
    @field:SerializedName("ip_address") private val ipAddress: String,
    @field:SerializedName("device_id") private val token: String?,
    @field:SerializedName("lat_long") private val latLong: LatLong)