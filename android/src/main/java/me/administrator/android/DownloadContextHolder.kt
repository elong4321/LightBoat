package me.administrator.android

import android.content.Context
import com.elong.lightboat.AndroidSystemFacade
import com.elong.lightboat.download.DownloadContext
import com.elong.lightboat.download.DownloadDispatcher
import okhttp3.OkHttpClient

class DownloadContextHolder {
    var downloadContext: DownloadContext? = null

    fun create(context: Context) {
        val dispatcher = DownloadDispatcher()
        val httpClient = OkHttpClient.Builder().build()
        downloadContext = DownloadContext.Builder().dispatcher(dispatcher).httpClient(httpClient)
            .systemFacade(AndroidSystemFacade(context.applicationContext)).build()
    }

    fun download() {
        downloadContext?.dispatcher?.download("https://curl.se/download/curl-7.77.0.zip")
    }
}