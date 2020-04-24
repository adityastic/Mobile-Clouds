package com.adityagupta.mobileclouds.utils

import android.content.Context
import android.util.Log
import com.adityagupta.mobileclouds.apis.ApiClient
import com.adityagupta.mobileclouds.apis.ApiInterface
import com.adityagupta.mobileclouds.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RetrofitHelper {
    private const val TAG = "RetrofitHelper"

    fun updateTokenOnServer(context: Context, user: String?, ipAddress: String, lat: String?, long: String?, token: String?) {
        if (user != "none") {
            val apiService = ApiClient.getClient(context)!!.create(ApiInterface::class.java)
            apiService.updateRefreshTokenOnServer(DeviceModel(user, ipAddress, token, LatLong(lat, long)))
                .enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        Log.e(TAG, "onResponse: Got Succ")
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Log.e(TAG, "onResponse: Got Fail", t)
                    }
                })
        }
    }

    fun getMyJobs(context: Context, callback: Callback<MyJobs?>) {
        val apiService = ApiClient.getClient(context)!!.create(ApiInterface::class.java)
        apiService.getMyJobs(PrefsHelper().getName(context))
            .enqueue(callback)
    }

    fun getSavedJobs(context: Context, callback: Callback<MySavedJobs?>){
        val apiService = ApiClient.getClient(context)!!.create(ApiInterface::class.java)
        apiService.getSavedJobs(PrefsHelper().getName(context))
            .enqueue(callback)
    }

    fun addMyJob(context: Context, fileName: String?, size: Long, callback: Callback<ResponseBody?>) {
        val apiService = ApiClient.getClient(context)!!.create(ApiInterface::class.java)
        apiService.addMyJob(SendJob(PrefsHelper().getName(context), fileName, size))
            .enqueue(callback)
    }
}