package com.elong.lightboat.download

import java.io.File
import java.io.IOException
import kotlin.jvm.Throws

interface ChunkWriter {
    @Throws(IOException::class)
    fun open(file: File)

    @Throws(IOException::class)
    fun write(chunk: DownloadTask.DownloadChunk): Int

    @Throws(IOException::class)
    fun position(): Long

    @Throws(IOException::class)
    fun flush()

    @Throws(IOException::class)
    fun close()
}