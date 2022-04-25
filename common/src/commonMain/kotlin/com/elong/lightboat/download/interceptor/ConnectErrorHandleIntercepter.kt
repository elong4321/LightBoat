package com.elong.lightboat.download.interceptor

import com.elong.lightboat.download.*
import okhttp3.Request
import okhttp3.Response
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

class ConnectErrorHandleIntercepter: ConnectIntecepter {
    override fun intercept(chain: Interceptor.Chain<Request, Response>): Response {
        val connectChain = chain as? ConnectChain
        connectChain?.let {
            var response: Response?
            try {
                response = chain.proceed(connectChain.request)
            } catch (e: Throwable) {
                if (e is SocketTimeoutException || e is ConnectException) {
                    throw StopRequestException(Downloads.STATUS_CONNECT_ERROR, e.message, e)
                } else if (e !is StopRequestException) {
                    throw StopRequestException(Downloads.STATUS_UNKNOWN_ERROR, e.message, e)
                } else {
                    throw e
                }
            }
            val code = response.code
            when (code) {
                HttpURLConnection.HTTP_OK -> return response
                HttpURLConnection.HTTP_PARTIAL -> return response
                Downloads.HTTP_REQUESTED_RANGE_NOT_SATISFIABLE -> {
                    throw StopRequestException(
                        Downloads.HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, "Requested range not satisfiable",
                        HttpError(Downloads.HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, response.message)
                    )
                }
                HttpURLConnection.HTTP_UNAVAILABLE -> {
                    val msg = response.message
                    throw StopRequestException(code, msg, HttpError(code, msg))
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    val msg = response.message
                    throw StopRequestException(code, msg, HttpError(code, msg))
                }
                else -> StopRequestException.throwUnhandledHttpError(code, response.message)
            }
        }
        throwInterceptorSetupError(javaClass.simpleName, ConnectChain::class.java.simpleName)
    }
}