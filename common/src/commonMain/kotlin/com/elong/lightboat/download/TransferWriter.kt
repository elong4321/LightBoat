package com.elong.lightboat.download

import android.util.Log
import java.util.concurrent.BlockingQueue

class TransferWriter(val queue: BlockingQueue<DownloadTask.DownloadChunk>): DownloadFetcher.Writer {

    override fun write(chunk: DownloadTask.DownloadChunk): Int {
        queue.put(chunk)
//        Log.d("chunks", "transfer: ${chunk.type}, ${chunk.chunkId}, ${chunk.offset}")
        val buffer = chunk.data
        return buffer?.limit() ?: 0
    }
}