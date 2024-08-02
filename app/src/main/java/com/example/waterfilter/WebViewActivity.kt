package com.example.waterfilter

import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.waterfilter.utility.NetworkChangeListener

class WebViewActivity : AppCompatActivity(), NetworkChangeListener.NetworkChangeCallback {

    private lateinit var webView: WebView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val networkChangeListener = NetworkChangeListener(this)
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.webView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        token = intent.getStringExtra("TOKEN")

        webView.webViewClient = MyWebClient(token)
        webView.settings.javaScriptEnabled = true

        swipeRefreshLayout.setOnRefreshListener {
            loadUrlWithToken(token)
        }

        loadUrlWithToken(token)
    }


    private fun loadUrlWithToken(token: String?) {
        webView.loadUrl("http://rgd.amusoft.uz/mobile/$token/agent/index")
    }

    inner class MyWebClient(private val token: String?) : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            swipeRefreshLayout.isRefreshing = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            swipeRefreshLayout.isRefreshing = false
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            request?.url?.let { view?.loadUrl("http://rgd.amusoft.uz/mobile/$token/agent/index") }
            return true
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Toast.makeText(this@WebViewActivity, "Error: ${error?.errorCode} - ${error?.description}", Toast.LENGTH_LONG).show()
        }

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            super.onReceivedHttpError(view, request, errorResponse)
            Toast.makeText(this@WebViewActivity, "HTTP error: ${errorResponse?.statusCode} - ${errorResponse?.reasonPhrase}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkChangeListener)
    }

    override fun onNetworkConnected() {
        // Reload the WebView when the network is connected
        loadUrlWithToken(token)
    }
}
