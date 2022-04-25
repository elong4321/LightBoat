package com.elong.lightboat.download

import android.util.Log
import com.elong.lightboat.common.Pool
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

class RealFetcher(private val task: DownloadTask, private val bufferPool: Pool<ByteBuffer>)
    : DownloadFetcher.Fetcher {

    override fun fetch(input: InputStream): DownloadTask.DownloadChunk {
        val buffer = bufferPool.pick(null)
        val bytes = buffer.array()
        var readBytes: Int = -1
        var throwable: Throwable? = null
        var downloadChunk: DownloadTask.DownloadChunk? = null
        try {
            readBytes = input.read(bytes)
        } catch (e: IOException) {
            throwable = StopRequestException(Downloads.STATUS_CONNECT_ERROR, e.message, e)

        }
        if (throwable != null) {    //error case
            bufferPool.reuse(buffer)
            throw throwable
        } else if (readBytes < 0) {     //read to the end or the connection breaks
            bufferPool.reuse(buffer)
            downloadChunk = DownloadTask.DownloadChunk(DownloadTask.DownloadChunk.TYPE_DATA_END,
                task.chunkInfo?.id ?: 0L, null, 0L, 0, null)
        } else {
            //the code below works the same as flip()
            buffer.position(0)
            buffer.limit(readBytes)

            task.state.currentBytes += readBytes
            val offset: Long = task.state.offsetBytes + task.state.currentBytes
            downloadChunk = DownloadTask.DownloadChunk(DownloadTask.DownloadChunk.TYPE_DATA,
                task.chunkInfo?.id ?: 0L, buffer, offset, readBytes, null)
        }
        Log.d("chunks", "fetch: ${downloadChunk.offset}, ${readBytes}")
//        Log.d("chunks", "fetch: ${downloadChunk.type}, ${downloadChunk.chunkId}, ${downloadChunk.offset}")
        return downloadChunk
    }
}