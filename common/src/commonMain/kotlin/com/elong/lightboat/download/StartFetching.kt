package com.elong.lightboat.download

import android.util.Log
import okhttp3.Response
import okhttp3.internal.closeQuietly

class StartFetching(val task: DownloadTask, val multiThread: Boolean = true, var response: Response? = null)
    : Runnable {

    override fun run() {
        val dispatcher = task.context.dispatcher
        try {
            val state = task.state
            val totalBytes = state.totalBytes
            var chunkNums = BytesCalculator.calculateChunkNums(totalBytes)
            var downloadToken = task.downloadToken
            downloadToken.pending = true
            if (multiThread && chunkNums > 1) {
                //TODO: check whether there are already multi thread for this task

                //split chunks and folk threads
                val chunks = splitChunks(task.info, state.totalBytes, chunkNums)
                //TODO: save chunks to db
                for (index in 0 until chunkNums) {
                    var rsp: Response? = null
                    if (index == 0) {
                        rsp = response
                        response = null
                    }
                    val multiThreadTask = DownloadTask(task.context, task.info, chunks[index], downloadToken, rsp)
                    dispatcher.download(multiThreadTask)
                    dispatcher.startWriterFor(task)
                }
            } else {
                val singleThreadTask = DownloadTask(task.context, task.info, null, downloadToken, response)
                response = null
                dispatcher.download(singleThreadTask)
                dispatcher.startWriterFor(task)
            }
        } finally {
            response?.closeQuietly()
        }
    }

    fun splitChunks(info: DownloadInfo, totalBytes: Long, chunkNums: Int): List<ChunkInfo> {
        val chunks = ArrayList<ChunkInfo>(chunkNums)
        var chunkSize: Long = totalBytes / chunkNums
        var chunkSizeNums = chunkNums - 1
        var offset: Long = 0
        var chunkTotal: Long = 0
        Log.d("chunks", "total: $totalBytes, chunkNums: $chunkNums, chunkSize: $chunkSize")
        for (index in 0 until chunkNums) {
            //totalBytes of the last chunk may be large than chunkSize
            chunkTotal = if (index < chunkSizeNums) chunkSize else (totalBytes - chunkSize * chunkSizeNums)
            val chunkInfo = ChunkInfo(/* TODO:assign id */index.toLong(), info.id, offset, 0, chunkTotal,
                Downloads.STATUS_PENDING)
            chunks.add(chunkInfo)
            offset += chunkTotal + 1
//            Log.d("chunks", "index: $index, chunkTotal: $chunkTotal, chunk: $chunkInfo")
        }
        return chunks
    }
}