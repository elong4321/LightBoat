package com.elong.lightboat.download

import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class DownloadToken(val id: Long, val url: String) {
    @Volatile var pending = false
    val runningDownloads = AtomicInteger(0)
    val downloadDataQueue = LinkedBlockingQueue<DownloadTask.DownloadChunk>()
    var file: File? = null
    var writer: ChunkWriter? = null

    fun needWritering(): Boolean {
        return pending || runningDownloads.get() < 1
    }

    fun startDownload(): Int {
        val nums = runningDownloads.incrementAndGet()
        if (nums == 1) {
            pending = false
        }
        return nums
    }

    fun finishDownload(): Int {
        return runningDownloads.decrementAndGet()
    }
}