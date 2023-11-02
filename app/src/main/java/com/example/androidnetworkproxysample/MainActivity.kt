package com.example.androidnetworkproxysample

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.androidnetworkproxysample.service.MyVPNService
import com.example.androidnetworkproxysample.service.VpnServiceHelper


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
    }
}