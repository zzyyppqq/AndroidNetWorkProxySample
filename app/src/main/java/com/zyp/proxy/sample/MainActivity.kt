package com.zyp.proxy.sample

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.zyp.proxy.sample.service.LocalVpnService
import com.zyp.proxy.sample.service.ToyVpnService
import com.zyp.proxy.sample.util.OkHttpManager
import com.zyp.proxy.sample.util.ProxyManager
import com.zyp.proxy.sample.util.WebSocketManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.btn_proxy_start).setOnClickListener {
            ProxyManager.start()
        }

        findViewById<Button>(R.id.btn_proxy_end).setOnClickListener {
            ProxyManager.stop()
        }

         findViewById<Button>(R.id.btn_vpn_proxy_start).setOnClickListener {
             startVPN()
        }

        findViewById<Button>(R.id.btn_vpn_proxy_end).setOnClickListener {
            stopVPN()
        }

        findViewById<Button>(R.id.btn_http_request).setOnClickListener {
            GlobalScope.launch {
                OkHttpManager.run()
            }
        }


        findViewById<Button>(R.id.btn_websocket_connect).setOnClickListener {
            GlobalScope.launch {
                WebSocketManager.init("ws://192.168.9.103:8083/websocket/?request=e2lkOjE7cmlkOjI2O3Rva2VuOiI0MzYwNjgxMWM3MzA1Y2NjNmFiYjJiZTExNjU3OWJmZCJ9")

            }
        }

        findViewById<Button>(R.id.btn_websocket_send).setOnClickListener {
            GlobalScope.launch {
                //WebSocketUtil.send("aaaaa")
                WebSocketManager.ping()
            }
        }
    }

//    private val clazz =  ToyVpnService::class.java
    private val clazz =  LocalVpnService::class.java
    private fun startVPN() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, 0)
        } else {
            onActivityResult(0, RESULT_OK, null)
        }
    }

    private fun stopVPN() {
        val intent = Intent(this, clazz)
        stopService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            val prefix = packageName
            val intent: Intent = Intent(this, clazz)
                .putExtra("$prefix.ADDRESS", "")
                .putExtra("$prefix.PORT", "")
                .putExtra("$prefix.SECRET", "")
            startService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ProxyManager.stop()
        WebSocketManager.disconnect(1000, "disconnect")
    }
}