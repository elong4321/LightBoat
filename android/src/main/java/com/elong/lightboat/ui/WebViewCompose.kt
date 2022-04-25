package com.elong.lightboat.ui

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.elong.lightboat.util.WebUtils
import com.elong.lightboat.webview.LightBoatWebView

@Composable
fun AndroidWebView(url: MutableState<String>, modifier: Modifier?) {
//    val context = LocalContext.current
    var androidWebView: WebView?
    AndroidView(
        modifier = modifier ?: Modifier,
        factory = {
            val webview = LightBoatWebView(it)
            WebUtils.setupWebView(webview)
            androidWebView = webview
            webview
        }
    ) {
        if ("" != url.value) {
            it.loadUrl(url.value)
        }
    }
}