package com.zyp.proxy.sample.util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.internal.ws.RealWebSocket
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

object WebSocketManager {
    private var webSocket: WebSocket? = null
    fun init(websocketUrl: String) {
        val mClient = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(websocketUrl)
            .build()

        webSocket = mClient.newWebSocket(request, object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
            }
        })


    }

    fun pingCount() {
        (webSocket as? RealWebSocket)?.receivedPingCount()

    }

    fun ping() {
        //(webSocket as? WebSocketReader.FrameCallback)?.onReadPing()
        val byteArray = PingPongUtil.ping()
        webSocket?.send(byteArray.toByteString())

    }

    fun send(msg: ByteString) {
        webSocket?.send(msg)

    }

    fun send(msg: String) {
        webSocket?.send(msg)
    }


    fun disconnect(code: Int, reason: String) {
        webSocket?.close(code, reason)
    }
}