package com.zyp.proxy.sample.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class LocalVpnService : VpnService(), Runnable {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            Thread(this, "LocalVpnThread").start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        vpnInterface?.close()
    }

    override fun run() {
        try {
            // Step 1: 建立 VPN 虚拟网络接口
            vpnInterface = establishVPN()

            // Step 2: 打开虚拟网络接口的输入和输出流
            val inputStream = FileInputStream(vpnInterface!!.fileDescriptor)
            val outputStream = FileOutputStream(vpnInterface!!.fileDescriptor)

            // Step 3: 打开本地文件保存流量数据
            val pcapFile = File(getExternalFilesDir(null), "traffic.pcap")
            val pcapOutputStream = FileOutputStream(pcapFile)
            writePcapHeader(pcapOutputStream)

            // Step 4: 处理数据包
            val buffer = ByteArray(32767)
            while (isRunning) {
                val length = inputStream.read(buffer)
                if (length > 0) {
                    // 保存流量到 PCAP 文件
                    writePcapPacket(pcapOutputStream, buffer, length)

                    // 将数据包直接写回虚拟接口，确保网络请求正常
                    outputStream.write(buffer, 0, length)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            vpnInterface?.close()
        }
    }

    private fun establishVPN(): ParcelFileDescriptor? {
        val builder = Builder()
        builder.setSession("LocalVpnService")
            .addAddress("10.0.0.2", 24) // 虚拟 IP 地址
//            .addDnsServer("8.8.8.8") // 设置 DNS
            .addRoute("0.0.0.0", 0) // 捕获所有流量
            .setMtu(1400) // MTU 设置
        return builder.establish()
    }

    private fun writePcapHeader(out: FileOutputStream) {
        val header = byteArrayOf(
            0xd4.toByte(), 0xc3.toByte(), 0xb2.toByte(), 0xa1.toByte(), // Magic Number
            0x02, 0x00, // Version major
            0x04, 0x00, // Version minor
            0x00, 0x00, 0x00, 0x00, // GMT to local correction
            0xff.toByte(), 0xff.toByte(), 0x00, 0x00, // Max length
            0x01, 0x00, 0x00, 0x00 // Link-layer header type
        )
        out.write(header)
    }

    private fun writePcapPacket(out: FileOutputStream, data: ByteArray, length: Int) {
        val timestamp = System.currentTimeMillis()
        val seconds = (timestamp / 1000).toInt()
        val microseconds = ((timestamp % 1000) * 1000).toInt()

        // PCAP 包头
        val header = ByteBuffer.allocate(16)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(seconds) // 时间戳秒
        header.putInt(microseconds) // 时间戳微秒
        header.putInt(length) // 捕获长度
        header.putInt(length) // 原始长度
        out.write(header.array())

        // 写入数据包内容
        out.write(data, 0, length)
    }
}

