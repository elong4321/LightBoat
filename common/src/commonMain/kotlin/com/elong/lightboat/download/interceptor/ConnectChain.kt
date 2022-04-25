package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import com.elong.lightboat.download.Interceptor
import okhttp3.Request
import okhttp3.Response

typealias ConnectIntecepter = Interceptor<Request, Response, Interceptor.Chain<Request, Response>>

abstract class ConnectChain(task: DownloadTask, list: List<ConnectIntecepter>):
    BaseChain<Request, Response>(task, list) {
    abstract var request: Request
}