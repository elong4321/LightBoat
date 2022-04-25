package com.elong.lightboat.download

class StopRequestException(val finalStatus: Int, message: String? = null, cause: Throwable? = null):
    Exception(message, cause) {
    companion object {
        fun throwUnhandledHttpError(code: Int, msg: String?): StopRequestException {
            val error = "Unhandled HTTP response: $code $msg"
            when(code) {
                in 400..599 -> throw StopRequestException(code, error, HttpError(code, msg))
                in 300..399 -> throw StopRequestException(Downloads.STATUS_UNHANDLED_REDIRECT, error, HttpError(code, msg))
                else -> throw StopRequestException(Downloads.STATUS_UNHANDLED_HTTP_CODE, error, HttpError(code, msg))
            }
        }
    }
}

class HttpError(val httpCode: Int, message: String? = null): Exception(message)