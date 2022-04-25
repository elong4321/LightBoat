package com.elong.lightboat.download

import okhttp3.Request
import java.nio.charset.Charset
import java.security.MessageDigest

fun fileName(url: String): String {
//    val digest = MessageDigest.getInstance("MD5")
//    return digest.digest(url.toByteArray(Charset.defaultCharset())).toString()
    return "curl-7.77.0.zip"
}

fun fileName(request: Request): String {
    return fileName(request.url.toUrl().toString())
}