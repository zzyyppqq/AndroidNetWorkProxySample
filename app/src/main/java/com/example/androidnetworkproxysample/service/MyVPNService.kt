package com.example.androidnetworkproxysample.service

import android.content.Intent
import android.net.Network
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramSocket
import java.net.Socket

class MyVPNService : VpnService(), Runnable {
    companion object {
        private const val TAG = "MyVPNService"

        private const val MUTE_SIZE = 2560
    }

    private var vpnThread: Thread? = null

    private var isRunning = false

    private var mVPNInterface: ParcelFileDescriptor? = null

    private var mVPNOutputStream: FileOutputStream? = null
    private var mVPNInputStream: FileInputStream? = null

    private val mPacket = ByteArray(MUTE_SIZE)
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        VpnServiceHelper.onVpnServiceCreated(this)
        isRunning = true
        vpnThread = Thread(this)
        vpnThread?.start()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.i(TAG, "onStart")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        VpnServiceHelper.onVpnServiceDestroy()
        isRunning = false
        vpnThread?.interrupt()
        vpnThread = null
    }


    override fun run() {
        try {
            while (isRunning) {
                mVPNInterface = establishVPN()
                startStream()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            dispose()
        }
    }

    @Throws(java.lang.Exception::class)
    private fun startStream() {
        var size = 0
        mVPNOutputStream = FileOutputStream(mVPNInterface!!.fileDescriptor)
        mVPNInputStream = FileInputStream(mVPNInterface!!.fileDescriptor)
        while (size != -1 && isRunning) {
            var hasWrite = false
            size = mVPNInputStream!!.read(mPacket)
            if (size > 0) {
                hasWrite = onIPPacketReceived(size)
            }
            if (!hasWrite) {

            }
            Thread.sleep(100L)
        }
        mVPNInputStream?.close()
        disconnectVPN()
    }

    private fun onIPPacketReceived(size: Int): Boolean {
        Log.i(TAG, "onIPPacketReceived size: $size")
        return true
    }

    private fun dispose() {
        disconnectVPN()
        stopSelf()
        isRunning = false
    }

    private fun disconnectVPN() {
        try {
            mVPNInterface?.close()
            mVPNInterface = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mVPNOutputStream = null
    }

    private fun establishVPN(): ParcelFileDescriptor? {
        val builder = Builder()
        val ipAddress = ProxyConfig.Instance.defaultLocalIP
        builder.addAddress(ipAddress.Address, ipAddress.PrefixLength)
        Log.i(TAG, "addAddress: ${ipAddress.Address}, PrefixLength: ${ipAddress.PrefixLength}")
        builder.addDnsServer("8.8.8.8")
        builder.addRoute("0.0.0.0", 0)
        builder.setMtu(MUTE_SIZE)
        return builder.establish()

    }

    override fun protect(socket: Int): Boolean {
        return super.protect(socket)
        Log.i(TAG, "protect(socket: Int)")
    }

    override fun protect(socket: Socket?): Boolean {
        return super.protect(socket)
        Log.i(TAG, "protect(socket: Socket?)")
    }

    override fun protect(socket: DatagramSocket?): Boolean {
        return super.protect(socket)
        Log.i(TAG, "protect(socket: DatagramSocket?)")
    }

    override fun setUnderlyingNetworks(networks: Array<out Network>?): Boolean {
        Log.i(TAG, "setUnderlyingNetworks")
        return super.setUnderlyingNetworks(networks)
    }

    override fun onRevoke() {
        super.onRevoke()
        Log.i(TAG, "onRevoke")
    }

    fun setVpnRunningStatus(stopStatus: Boolean) {
        isRunning = stopStatus
    }

}