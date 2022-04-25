package com.elong.lightboat.download

object BytesCalculator {
    const val _1M = 1024 * 1024
    const val _5M = 5 * _1M
    const val _10M = 10 * _1M
    const val _50M = 50 * _1M
    const val _100M = 100 * _1M

    fun position(info: DownloadInfo): Long {
        return info.currentBytes
    }

    fun position(info: ChunkInfo): Long {
        return info.offsetBytes + info.currentBytes
    }

    fun position(state: DownloadTask.State): Long {
        return state.offsetBytes + state.currentBytes
    }

    fun isDownloadCompleted(info: DownloadInfo): Boolean {
        val chunks = info.chunks.toList()
        if (info.totalBytes > 0) {
            if (chunks.size > 0) {
                var totalChunkBytes = 0L
                for (chunk in chunks) {
                    totalChunkBytes += chunk.currentBytes
                }
                return info.totalBytes == totalChunkBytes
            } else {
                return info.currentBytes == info.totalBytes
            }
        }
        return false
    }

    fun calculateChunkNums(contentLength: Long): Int {
        return if (contentLength > _100M) 5 else if (contentLength > _50M) 4
        else if (contentLength > _5M) 3 else if (contentLength > _1M) 2 else 1
    }
}