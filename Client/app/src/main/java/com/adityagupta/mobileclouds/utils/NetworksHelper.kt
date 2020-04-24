package com.adityagupta.mobileclouds.utils

import java.net.Inet4Address
import java.net.NetworkInterface


object NetworksHelper {
    fun getIpAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }
}