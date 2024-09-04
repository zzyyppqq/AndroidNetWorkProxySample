package com.example.androidnetworkproxysample.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import java.net.Socket

object VpnServiceHelper {
    private var context: Context? = null
    const val START_VPN_SERVICE_REQUEST_CODE = 2015
    private var sVpnService: MyVPNService? = null

    @SuppressLint("StaticFieldLeak")
    fun onVpnServiceCreated(vpnService: MyVPNService) {
        sVpnService = vpnService
        if (context == null) {
            context = vpnService.applicationContext
        }
    }

    fun onVpnServiceDestroy() {
        sVpnService = null
    }

    fun getContext(): Context? {
        return context
    }

    fun changeVpnRunningStatus(context: Context?, isStart: Boolean) {
        if (context == null) {
            return
        }
        if (isStart) {
            val intent = VpnService.prepare(context)
            if (intent == null) {
                startVpnService(context)
            } else {
                if (context is Activity) {
                    context.startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE)
                }
            }
        } else if (sVpnService != null) {
            val stopStatus = false
            sVpnService?.setVpnRunningStatus(stopStatus)
        }
    }

    fun startVpnService(context: Context?) {
        if (context == null) {
            return
        }
        context.startService(Intent(context, MyVPNService::class.java))
    }

    fun protect(socket: Socket): Boolean {
        return sVpnService?.protect(socket)?:false
    }

}