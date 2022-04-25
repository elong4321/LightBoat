package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.Interceptor
import okhttp3.Request
import okhttp3.Response

class CallServerInterceptor: ConnectIntecepter {
    override fun intercept(chain: Interceptor.Chain<Request, Response>): Response {
        val connectChain = chain as? ConnectChain
        connectChain?.let {
            val httpClient = chain.task.context.httpClient
            return httpClient.newCall(it.request).execute()
        }
        throwInterceptorSetupError(javaClass.simpleName, ConnectChain::class.java.simpleName)
    }
}