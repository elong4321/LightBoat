package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import com.elong.lightboat.download.Interceptor
import okhttp3.Request
import okhttp3.Response

class ConnectChainFactory(private val request: Request, private val task: DownloadTask,
                          private val interceptors: List<ConnectIntecepter>? = null):
    Interceptor.Chain.Factory<Request, Response> {

    override fun create(): Interceptor.Chain<Request, Response> {
        val list = interceptors ?: listOf( StartFetchingInterceptor(),
            LengthAndRangeInterceptor(), ConnectErrorHandleIntercepter(), HeaderInterceptor(),
            CallServerInterceptor() )
        return RealConnectChain(request, task, list)
    }
}