package com.adityagupta.mobileclouds.utils

import android.content.Context

class PrefsHelper {
    fun getName(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("LOGGED", "none")
    }

    fun setName(context: Context, value: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString("LOGGED", value).apply()
    }

    fun getServerIP(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("SERVER-IP", "0.0.0.0")
    }

    fun setServerIP(context: Context, value: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString("SERVER-IP", value).apply()
    }

    fun getLat(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("LAT", "0.0")
    }

    fun setLat(context: Context, value: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString("LAT", value).apply()
    }

    fun getLong(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("LONG", "0.0")
    }

    fun setLong(context: Context, value: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString("LONG", value).apply()
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("TOKEN", "0.0")
    }

    fun setToken(context: Context, value: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString("TOKEN", value).apply()
    }

    companion object {
        @get:Synchronized
        var instance: PrefsHelper? = null
            get() {
                if (field == null) {
                    field = PrefsHelper()
                }
                return field
            }
            private set
        private const val PREFS_NAME = "default_preferences"
    }
}