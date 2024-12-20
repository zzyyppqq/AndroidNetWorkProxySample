package com.zyp.proxy.sample.service

import android.content.Intent
import android.net.Network
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramSocket
import java.net.Socket


class MyVpnService : VpnService(), Runnable {
    companion object {
        private const val TAG = "MyVPNService"

        const val MUTE_SIZE = 2560
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
        if (mVPNInterface == null) {
            mVPNInterface = establishVPN()
        }
        isRunning = true
        vpnThread = Thread(this)
        vpnThread?.start()
    }

    override fun run() {
        try {
            Log.i(TAG, "MyVPNService work thread is Running...")
            waitUntilPrepared()
            while (isRunning) {
                startStream()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            dispose()
        }
    }

    private fun waitUntilPrepared() {
        // 如果 VPN 应用程序已准备好或者用户之前已同意 VPN 应用程序，则prepare()方法返回 null
        while (prepare(this) != null) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
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
        return Builder()
            .addAddress("10.8.0.2", 32)
            .addDnsServer("8.8.8.8")
            .addRoute("0.0.0.0", 0)
            .setMtu(MUTE_SIZE)
            .establish()
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
        isRunning = false
        vpnThread?.interrupt()
        vpnThread = null
    }


    fun setVpnRunningStatus(stopStatus: Boolean) {
        isRunning = stopStatus
    }

}