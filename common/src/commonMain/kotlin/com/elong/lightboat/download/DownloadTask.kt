package com.elong.lightboat.download

import android.util.Log
import com.elong.lightboat.download.interceptor.ConnectChainFactory
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.nio.ByteBuffer

class DownloadTask(val context: DownloadContext, val info: DownloadInfo, val chunkInfo: ChunkInfo? = null,
                   val downloadToken: DownloadToken, val inheritedResponse: Response? = null) : Runnable {

    var state = State(info, chunkInfo)

    override fun run() {
//        TODO("check conditions before")
        if (BytesCalculator.isDownloadCompleted(info)) {
//            TODO("check chunks and integrity of the download file and report to db")
            info.currentBytes = info.totalBytes
            return
        }

        downloadToken?.startDownload()

        var response: Response? = inheritedResponse
        var toCloseResponse = true
        try {
            if (response == null) {
                //make request
                val request = Request.Builder().url(state.url).get().build()
                val connectChain = ConnectChainFactory(request, this).create()
                response = connectChain.proceed(request)
            }
            //start fetching
            if (state.startFetching != null) {
                state.startFetching?.let {
                    toCloseResponse = it.response != response
                    context.dispatcher.update(it)
                }
                return
            }
            //fetch data
            val input = response.body?.byteStream()
            if (input != null) {
                val fetcher = RealFetcher(this, context.dispatcher.bufferPool)
                val writer = TransferWriter(downloadToken.downloadDataQueue)
                var downloadChunk: DownloadChunk? = null
                do {
                    downloadChunk = fetcher.fetch(input)
                    writer.write(downloadChunk)
                } while (downloadChunk != null && downloadChunk.type == DownloadChunk.TYPE_DATA)
                Log.d("chunks", "break loop: ${chunkInfo?.id}")
            } else {
                throw StopRequestException(Downloads.STATUS_HTTP_DATA_ERROR, "empty response body")
            }
        } catch (e: StopRequestException) {
            //TODO: stop other download?
            e.printStackTrace()
        } catch (e: Throwable) {
            //TODO: handle e
            e.printStackTrace()
        } finally {
            if (toCloseResponse) {
                response?.closeQuietly()
            }
            downloadToken?.finishDownload()
        }
    }

    class State{
        var requestUrl = ""
        var url = ""
//        var contentLength = 0L  //totalBytes means contentLength in our download case
        var offsetBytes = 0L
        var currentBytes = 0L
        var totalBytes = 0L
        var acceptRanges = false
        /**
         * it will be assigned as true in these cases as below:
         * 1. lunch partial download when current_bytes > 0 in downloads table of database
         * 2. lunch partial downloads due to rows in chunks table of database
         * 3. response code from server == 206
         *
         * it may changes when in different stages
         */
        var partial = false

        var contentDisposition = ""
        var contentLocation = ""
        var noIntegrity = false
        var mimeType = ""

        var startFetching: StartFetching? = null

        constructor (info: DownloadInfo, chunkInfo: ChunkInfo?) {
            requestUrl = info.url
            url = requestUrl
            //TODO: finish it

            if (chunkInfo != null) {
                offsetBytes = chunkInfo.offsetBytes
                currentBytes = chunkInfo.currentBytes
                totalBytes = chunkInfo.totalBytes
                partial = true
            } else {
                offsetBytes = 0
                currentBytes = info.currentBytes
                totalBytes = info.totalBytes
                partial = BytesCalculator.position(this) > 0
            }
        }
    }

    class DownloadChunk(
        var type: Byte = TYPE_DATA,
        var chunkId: Long = 0L,
        var data: ByteBuffer? = null,
        var offset: Long = 0L,
        var finalStatus: Int = 0,
        var error: String? = null
    ) {
        companion object {
            val TYPE_DATA: Byte = 1
            val TYPE_DATA_END: Byte = 2
            val TYPE_END: Byte = 3
        }

        fun reset() {
            data = null
            offset = 0L
        }
    }
}