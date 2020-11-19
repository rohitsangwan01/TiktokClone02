package com.pvaindia.tiktokclone0

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {

    var webView: WebView? = null
    var  url: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        supportActionBar?.hide()

        url = intent.getStringExtra("url")
//        Log.i("item_comment", url)

        webView = findViewById(R.id.webView)
        webView?.webViewClient = WebViewClient()
        webView?.loadUrl(url)


    }
}