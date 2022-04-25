package com.elong.lightboat.download

import java.io.InputStream

interface DownloadFetcher {
    interface Fetcher {
        fun fetch(input: InputStream): DownloadTask.DownloadChunk
    }

    interface Writer {
        fun write(chunk: DownloadTask.DownloadChunk): Int
    }
}