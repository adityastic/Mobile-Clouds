package com.adityagupta.mobileclouds.apis

import com.adityagupta.mobileclouds.model.DeviceModel
import com.adityagupta.mobileclouds.model.MyJobs
import com.adityagupta.mobileclouds.model.MySavedJobs
import com.adityagupta.mobileclouds.model.SendJob
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("/api/v1/device")
    fun updateRefreshTokenOnServer(@Body device: DeviceModel?): Call<ResponseBody?>

    @POST("/api/v1/getMyJobs")
    fun getMyJobs(@Body slug: String?): Call<MyJobs?>

    @POST("/api/v1/getSavedJobs")
    fun getSavedJobs(@Body slug: String?): Call<MySavedJobs?>

    @POST("/api/v1/addMyJob")
    fun addMyJob(@Body job: SendJob): Call<ResponseBody?>
}