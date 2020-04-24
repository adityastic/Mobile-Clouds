package com.adityagupta.mobileclouds.apis

import android.content.Context
import com.adityagupta.mobileclouds.utils.PrefsHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null
    fun getClient(context: Context): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("http://" + PrefsHelper().getServerIP(context) + ":5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}