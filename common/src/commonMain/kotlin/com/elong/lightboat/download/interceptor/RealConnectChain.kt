package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.DownloadTask
import okhttp3.Request

class RealConnectChain(override var request: Request, task: DownloadTask,
                       list: List<ConnectIntecepter>) :
    ConnectChain(task, list) {
}