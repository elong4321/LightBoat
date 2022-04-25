package com.elong.lightboat.download

import android.util.LongSparseArray
import com.elong.lightboat.common.AtomicArrayList
import com.elong.lightboat.common.AtomicLock
import java.util.concurrent.atomic.AtomicBoolean

data class DownloadInfo(
    @Volatile var id: Long,
    @Volatile var url: String,
    @Volatile var noIntegrity: Boolean = false,
    @Volatile var hint: String? = "",
    @Volatile var fileName: String? = "",
    @Volatile var mimeType: String? = "",
    @Volatile var destination: Int = 0,
    @Volatile var visibility: Int = 0,
    @Volatile var status: Int = Downloads.STATUS_PENDING,
    @Volatile var numFailed: Int = 0,
    @Volatile var retryAfter: Int = 0,
    @Volatile var lastMod: Int = 0,
    @Volatile var callerPackage: String? = "",
    @Volatile var callerClass: String? = "",
    @Volatile var cookies: String? = "",
    @Volatile var userAgent: String? = "",
    @Volatile var referer: String? = "",
    @Volatile var totalBytes: Long = 0,
    @Volatile var currentBytes: Long = 0,
    @Volatile var etag: String? = "",
    @Volatile var uid: Int = 0,
    @Volatile var deleted: Boolean = false,
    @Volatile var allowedNetworkTypes: Int = Integer.MAX_VALUE,
    @Volatile var allowRoaming: Boolean = false,
    @Volatile var allowMetered: Boolean = false,
    @Volatile var title: String? = "",
    @Volatile var description: String? = "",
    @Volatile var bypassRecommendedSizeLimit: Int = 0,
    @Volatile var fuzz: Int = 0,
    @Volatile var errorMsg: String? = "",
    @Volatile var requestHeaders: List<Pair<String, String>> = arrayListOf(),
    val chunks: AtomicArrayList<ChunkInfo> = AtomicArrayList()
)

data class ChunkInfo(
    @Volatile var id: Long,
    @Volatile var downloadId: Long,
    @Volatile var offsetBytes: Long,
    @Volatile var currentBytes: Long,
    @Volatile var totalBytes: Long,
    @Volatile var status: Int
)