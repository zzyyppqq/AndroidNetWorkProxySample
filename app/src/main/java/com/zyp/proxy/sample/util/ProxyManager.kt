package com.zyp.proxy.sample.util

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.lightbody.bmp.BrowserMobProxy
import net.lightbody.bmp.BrowserMobProxyServer
import org.slf4j.LoggerFactory


object ProxyManager: Runnable {
    const val TAG = "ProxyManager"
    private var proxy: BrowserMobProxy? = null
    private var readThread: Thread? = null
    private var isRunning = true

    init {
        val logger = LoggerFactory.getLogger(ProxyManager::class.java)
        logger.info("hello world");
    }

    fun start() {
        GlobalScope.launch {
            proxy = BrowserMobProxyServer()
            proxy?.start(8888)

            proxy?.addRequestFilter { request, contents, messageInfo ->
                Log.i(TAG, "Request: $request")
                Log.i(TAG, "Request contents contentType: ${contents.contentType}, charset: ${contents.charset}, textContents: ${contents.textContents}, isText: ${contents.isText}, binaryContents: ${contents.binaryContents}")
                val headers = request.headers()
                Log.i(TAG, "Request Headers: ${headers.entries()}")

                null
            }
            proxy?.addResponseFilter { response, contents, messageInfo ->
                Log.i(TAG, "Response $response")
                Log.i(TAG, "Response contents: $contents")
                Log.i(TAG, "Response messageInfo: $messageInfo")
            }
            isRunning = true
            readThread = Thread(ProxyManager)
            readThread?.start()
        }

    }

    fun stop() {
        isRunning = false
        readThread?.interrupt()
        readThread = null
        proxy?.stop()
        proxy = null
    }


    override fun run() {
        while (isRunning && !Thread.interrupted()) {
            try {
                readHarEntry()

                Thread.sleep(100L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun readHarEntry() {
        // 获取所有的HarEntry对象（请求和响应对）
        val har = proxy?.har
        val entries = har?.log?.entries
        Log.i(TAG, "entries: ${entries?.size}")
        // 遍历HarEntry对象，获取请求和响应信息
        entries?.forEach { entry ->
            val request = entry.request
            val response = entry.response

            // 分别获取请求和响应的URL、HTTP方法、状态码等信息
            val url = request.url
            val method = request.method
            val statusCode = response.status

            // 进一步获取请求和响应的头部、内容等信息
            val requestHeaders = request.headers
            val responseHeaders = response.headers
            val comment: String = request.comment
            val responseBody = response.content.text
            Log.i(TAG, "request: $request, response: $response")
            Log.i(TAG, "url: $url, method: $method, statusCode: $statusCode")
        }
    }


}