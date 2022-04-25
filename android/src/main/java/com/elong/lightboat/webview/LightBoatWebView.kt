package com.elong.lightboat.webview

import android.content.Context
import android.webkit.WebView
import com.elong.lightboat.util.WebUtils

class LightBoatWebView: WebView {

    constructor(context: Context) : super(context) {
        WebUtils.setupWebView(webView = this);
    }
}