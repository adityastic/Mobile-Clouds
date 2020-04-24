package com.adityagupta.mobileclouds.model

import com.google.gson.annotations.SerializedName

data class SavedJob(
    val user: String,
    @field:SerializedName("total_jobs") val totalJobs: Int)
