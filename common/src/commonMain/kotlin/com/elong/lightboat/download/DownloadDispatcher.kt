package com.elong.lightboat.download

import com.elong.lightboat.common.Pool
import okhttp3.OkHttpClient
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.*

class DownloadDispatcher() {
    var context: DownloadContext? = null
    private var downloadExecutor: ExecutorService? = null
    private var syncExecutor: ExecutorService? = null
    private var writerExecutor: ExecutorService? = null
    private var httpClient: OkHttpClient? = null
    var bufferPool: Pool<ByteBuffer> = BufferPool(5)    //TODO: capacity

    init {
        downloadExecutor = ThreadPoolExecutor(0, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
            SynchronousQueue())
        syncExecutor = Executors.newSingleThreadExecutor()
        //TODO: proceesor.core * 2 + 1
        writerExecutor = ThreadPoolExecutor(0, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
            SynchronousQueue())
//        TODO("config client")
        httpClient = OkHttpClient()
    }

    fun download(url: String) {
        context?.let {
            //TODO: insert to db to trigger download
            val info = DownloadInfo(0, url)
            download(DownloadTask(it, info, null, DownloadToken(0, url), null))
        }
    }

    fun download(task: DownloadTask) {
        downloadExecutor?.execute(task)
    }

    fun update(runnable: Runnable) {
        syncExecutor?.execute(runnable)
    }

    fun startWriterFor(task: DownloadTask) {
        task.downloadToken?.let {
            if (it.file == null) {
                it.file = if (!task.info.fileName.isNullOrBlank()) File(task.info.fileName) else
                    File(context!!.systemFacade.downloadDir(task.state.mimeType), fileName(task.state.url))
            }
            if (!it.file!!.exists()) {
                it.file!!.createNewFile()
            }
            if (it.writer == null) {
                it.writer = FileChannelWriter()
            }
            val writerTask = WriteTask(it)
            writerExecutor?.execute(writerTask)
        }

    }
}
