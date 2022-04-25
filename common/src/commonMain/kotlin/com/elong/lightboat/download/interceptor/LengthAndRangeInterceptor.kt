package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.*
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection

class LengthAndRangeInterceptor: ConnectIntecepter {
    override fun intercept(chain: Interceptor.Chain<Request, Response>): Response {
        val connectChain = chain as? ConnectChain
        connectChain?.let {
            val state = it.task.state

            var request = connectChain.request
            //add range header to request
            val currentOffset = BytesCalculator.position(state)
            var wantPartial = state.partial
            if (currentOffset > 0) {
                var rangeStr = "bytes=$currentOffset-"
                if (state.totalBytes > state.currentBytes) {
                    rangeStr = "$rangeStr${state.totalBytes}"
                }
                request = request.newBuilder().rangeHeader(rangeStr).build()
                wantPartial = true
            }

            val response = chain.proceed(request)

            val transferEncoding = response.header(HTTP_TRANSFER_ENCODING)
            if (transferEncoding == null) {
                state.totalBytes = response.header(HTTP_CONTENT_LENGTH)?.toLong() ?: -1L
            } else {
                val msg = "Ignoring Content-Length since Transfer-Encoding is also defined"
                state.totalBytes = -1
            }
            state.partial = response.code == HttpURLConnection.HTTP_PARTIAL
//            if (state.partial) {
//                val contentRange = response.header(HTTP_CONTENT_RANGE)
//            } else {
//                state.totalBytes = state.contentLength
//            }
            val noSizeInfo = (state.totalBytes == -1L) && (transferEncoding == null ||
                    !HTTP_CHUNKED.contentEquals(transferEncoding, true))
            if (!state.noIntegrity && noSizeInfo) {
                throw StopRequestException(Downloads.STATUS_CANNOT_RESUME,
                    "can't know size of download, giving up"
                )
            }
            //TODO: review here
            if (wantPartial != it.task.state.partial) {
                throw StopRequestException(Downloads.STATUS_CANNOT_RESUME,
                    "want partial content:$wantPartial, but get partial content:${it.task.state.partial}")
            }
            state.acceptRanges = HTTP_BYTES.equals(response.header(HTTP_ACCEPT_RANGES), true)

            return response
        }
        throwInterceptorSetupError(javaClass.simpleName, ConnectChain::class.java.simpleName)
    }
}

private fun Request.Builder.rangeHeader(range: String) = apply {
    header("Range", range)
}