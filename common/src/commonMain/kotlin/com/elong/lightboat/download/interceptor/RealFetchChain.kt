package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import okhttp3.Response

class RealFetchChain(override val response: Response, task: DownloadTask, list: List<FetchInterceptor>):
    FetchChain(task, list) {

}