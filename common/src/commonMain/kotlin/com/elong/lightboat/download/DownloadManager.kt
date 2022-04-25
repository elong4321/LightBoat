package com.elong.lightboat.download

interface DownloadManager {
    fun download(url: String): Long
    fun pause(id: Long)
    fun resume(id: Long)
    fun remove(id: Long, delete: Boolean)
    fun query(id: Long): DownloadInfo
}