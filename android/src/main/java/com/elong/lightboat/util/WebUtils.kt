package com.elong.lightboat.util

import android.webkit.WebView
import com.elong.lightboat.webview.BrowserWebChromeClient
import com.elong.lightboat.webview.BrowserWebViewClient

object WebUtils {

    fun setupWebView(webView: WebView) {
        webView.webViewClient = BrowserWebViewClient()
        webView.webChromeClient = BrowserWebChromeClient()
        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = true
        }
    }
}