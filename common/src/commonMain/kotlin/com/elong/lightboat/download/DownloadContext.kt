package com.elong.lightboat.download

import okhttp3.OkHttpClient
import java.util.concurrent.ExecutorService

class DownloadContext(val dispatcher: DownloadDispatcher, val httpClient: OkHttpClient,
                      val systemFacade: SystemFacade) {

    class Builder {
        private var dispatcher: DownloadDispatcher? = null
        private var httpClient: OkHttpClient? = null
        private var systemFacade: SystemFacade? = null

        fun dispatcher(dispatcher: DownloadDispatcher): Builder {
            this.dispatcher = dispatcher
            return this
        }

        fun httpClient(httpClient: OkHttpClient): Builder {
            this.httpClient = httpClient
            return this
        }

        fun systemFacade(systemFacade: SystemFacade): Builder {
            this.systemFacade = systemFacade
            return this
        }

        fun build(): DownloadContext {
            if (dispatcher == null) {
                throw IllegalArgumentException("dispatcher is a must")
            }
            if (httpClient == null) {
                throw IllegalArgumentException("httpClient is a must")
            }
            if (systemFacade == null) {
                throw IllegalArgumentException("systemFacade is a must")
            }

            val context = DownloadContext(dispatcher!!, httpClient!!, systemFacade!!)
            dispatcher!!.context = context
            return context
        }
    }
}