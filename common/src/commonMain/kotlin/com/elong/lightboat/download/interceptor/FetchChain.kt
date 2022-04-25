package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import com.elong.lightboat.download.Interceptor
import okhttp3.Response

typealias FetchInterceptor = Interceptor<Response, Long, Interceptor.Chain<Response, Long>>

abstract class FetchChain(task: DownloadTask, list: List<FetchInterceptor>):
    BaseChain<Response, Long>(task, list) {
    abstract val response: Response
}