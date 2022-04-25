package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.*
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection

class StartFetchingInterceptor: ConnectIntecepter {
    override fun intercept(chain: Interceptor.Chain<Request, Response>): Response {
        val connectChain = chain as? ConnectChain
        connectChain?.let {
            val wantPartial = it.task.state.partial
            val response = it.proceed(it.request)
            /*
            1.!wantPartial && !it.task.state.partial -> a new download task but not a continuing one
            2.it.task.state.currentBytes == 0L -> not fetch any data yet
            3.response.code == 200 -> this is connecting and querying at first time
             */
            val startFetching = !wantPartial && !it.task.state.partial && it.task.state.currentBytes == 0L
                    && response.code == HttpURLConnection.HTTP_OK
            if (startFetching) {
                //TODO: make sure we want multi-thread download
                var multiThreadDownload = HTTP_BYTES.equals(response.header(HTTP_ACCEPT_RANGES), true)
                it.task.state.startFetching = StartFetching(it.task, multiThreadDownload, response)
            }
            return response
        }
        throwInterceptorSetupError(javaClass.simpleName, ConnectChain::class.java.simpleName)
    }
}