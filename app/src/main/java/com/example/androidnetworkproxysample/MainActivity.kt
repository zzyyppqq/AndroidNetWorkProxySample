package com.example.androidnetworkproxysample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.androidnetworkproxysample.service.VpnServiceHelper
import com.example.androidnetworkproxysample.util.OkHttpManager
import com.example.androidnetworkproxysample.util.WebSocketManager
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
             VpnServiceHelper.changeVpnRunningStatus(this,true)
        }

        findViewById<Button>(R.id.btn_vpn_proxy_end).setOnClickListener {
            VpnServiceHelper.changeVpnRunningStatus(this,false)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == VpnServiceHelper.START_VPN_SERVICE_REQUEST_CODE && resultCode == RESULT_OK) {
            VpnServiceHelper.startVpnService(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ProxyManager.stop()
        WebSocketManager.disconnect(1000, "disconnect")
    }
}