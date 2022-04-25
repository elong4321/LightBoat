package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import com.elong.lightboat.download.Interceptor
import okhttp3.Response

class FetchChainFactory(private val response: Response, private val task: DownloadTask,
                        private val interceptors: List<FetchInterceptor>? = null):
    Interceptor.Chain.Factory<Response, Long> {

    override fun create(): Interceptor.Chain<Response, Long> {
        val list = interceptors ?: listOf<FetchInterceptor>()
        return RealFetchChain(response, task, list)
    }
}