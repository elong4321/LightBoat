package com.elong.lightboat.download

import android.util.Log
import java.io.File
import java.nio.channels.FileChannel

class ChunkChannelWriter(private val fileChannel: FileChannel): ChunkWriter {

    override fun open(file: File) {
        //do nothing
    }

    override fun write(chunk: DownloadTask.DownloadChunk): Int {
        //TODO: atomically operate info&data together
        chunk.data?.let { it ->
            val pos = chunk.offset
            Log.d("chunks", "write: ${chunk.chunkId}, ${chunk.type}, ${chunk.offset}, ${it.position()} bytes=${it.limit()}, ${pos} == ${fileChannel.position()} ${toString()}@${Thread.currentThread().id}")
            if (fileChannel.position() != pos) {
                fileChannel.position(pos)
            }
            return fileChannel.write(it)
        }
        return 0
    }

    override fun flush() {
        fileChannel.force(false)
    }

    override fun close() {
        fileChannel.close()
    }

    override fun position(): Long {
        return fileChannel.position()
    }
}