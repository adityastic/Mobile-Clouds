package com.adityagupta.mobileclouds.services

import android.content.Intent
import android.util.Log
import com.adityagupta.mobileclouds.MainActivity
import com.adityagupta.mobileclouds.utils.NotificationHelper
import com.adityagupta.mobileclouds.utils.NetworksHelper
import com.adityagupta.mobileclouds.utils.PrefsHelper
import com.adityagupta.mobileclouds.utils.RetrofitHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile


class FirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val payload = JSONObject(remoteMessage.data as Map<*, *>)
            try {
                NotificationHelper(applicationContext)
                    .createNotification(
                        payload.getString("title"),
                        payload.getString("message"),
                        Intent(this, MainActivity::class.java))
                storeFile(storageFile, payload.getLong("size"))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        PrefsHelper().setToken(applicationContext, token)

        RetrofitHelper.updateTokenOnServer(applicationContext,
            PrefsHelper().getName(applicationContext),
            NetworksHelper.getIpAddress(),
            PrefsHelper().getLat(applicationContext),
            PrefsHelper().getLong(applicationContext),
            PrefsHelper().getToken(applicationContext))
    }

    fun storeFile(file: File?, fileSize: Long) {
        var newSparseFile: RandomAccessFile? = null
        try {
            newSparseFile = RandomAccessFile(File.createTempFile("rec", "", applicationContext.getExternalFilesDir(null)), "rw")
            newSparseFile.setLength(fileSize)
        } catch (e: Exception) {
            Log.e("DEBUG", "error while creating file:$e")
        } finally {
            if (newSparseFile != null) try {
                newSparseFile.close()
            } catch (e: IOException) {
                Log.e("DEBUG", "error while closing file:$e")
            }
        }
    }

    companion object {
        private const val TAG = "FirebaseService"

        private val storageFile: File? = null;
    }
}