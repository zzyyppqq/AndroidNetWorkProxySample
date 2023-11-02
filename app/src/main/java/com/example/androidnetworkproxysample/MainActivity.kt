package com.example.androidnetworkproxysample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

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
    }

    override fun onDestroy() {
        super.onDestroy()
        ProxyManager.stop()
    }
}