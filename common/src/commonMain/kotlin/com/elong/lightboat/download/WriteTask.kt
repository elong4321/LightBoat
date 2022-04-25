package com.elong.lightboat.download

import android.util.Log
import java.io.IOException

class WriteTask(val token: DownloadToken): Runnable {

    override fun run() {
        var downloadChunk: DownloadTask.DownloadChunk? = null
        var bytes: Int = -1
        try {
            val file = token.file
            val writer = token.writer
            if (file == null || writer == null) {
                throw IllegalArgumentException("file or writer is null")
            }

            writer.open(file)
            do {
                val s = token.downloadDataQueue.size
                Log.d("chunks_writer", "queue size: ${s} thread:${Thread.currentThread().toString()}")
                downloadChunk = token.downloadDataQueue.take()
                Log.d("chunks_writer", "after taking: $s thread:${Thread.currentThread().toString()}")
                when (downloadChunk.type) {
                    DownloadTask.DownloadChunk.TYPE_DATA -> {
                        bytes = writer.write(downloadChunk)
                        if (bytes < 0) {
                            throw IOException("can't write bytes into file")
                        }
                    }
                    DownloadTask.DownloadChunk.TYPE_DATA_END -> {
                        //TODO: update chunk result to db
                        Log.d("chunks", "writeTask: ${downloadChunk.type}")
                    }
                    DownloadTask.DownloadChunk.TYPE_END -> {
                        //TODO: update chunk result to db
                        Log.d("chunks", "writeTask: ${downloadChunk.type}")
                    }
                }
            } while (downloadChunk != null)
            writer.flush()
            writer.close()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}