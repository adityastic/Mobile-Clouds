package com.adityagupta.mobileclouds.model

import com.google.gson.annotations.SerializedName

data class MyJob(
    val file: String?,
    @field:SerializedName("saved_at_ip") val savedAt: List<String>
)
