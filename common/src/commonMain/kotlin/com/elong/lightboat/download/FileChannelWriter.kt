package com.elong.lightboat.download

import com.elong.lightboat.common.AtomicLock
import java.io.File
import java.io.RandomAccessFile

class FileChannelWriter(val maxChunkWriters: Int = 3): ChunkWriter {
    private var file: File? = null
//    private var f: RandomAccessFile? = null
    private val availableWriters = ArrayList<ChunkWriter>(2)
    private val busyWriters = ArrayList<ChunkWriter>()
    private var totalWriters = 0
    private val lock = AtomicLock()
    @Volatile private var closed = false
    override fun open(file: File) {
        lock.lock {
//            f = RandomAccessFile(file, "rwd")
            this.file = file
            closed = false
        }
    }

    override fun write(chunk: DownloadTask.DownloadChunk): Int {
        return if (!closed) {
            val writer = lock.lock { pickWriterToWork(chunk) }
            val bytes = writer?.let {
                val b = it.write(chunk)
                lock.lock { freeWriter(writer) }
                b
            }
            bytes ?: 0
        }  else {
            0
        }
    }

    private fun pickWriterToWork(chunk: DownloadTask.DownloadChunk): ChunkWriter? {
        var preferOne: ChunkWriter? = null
        chunk.data?.let { data ->
            val downloadOffset = chunk.offset
            for (item in availableWriters) {
                if (preferOne == null) {
                    preferOne = item
                } else if ((downloadOffset - item.position()) < (downloadOffset - preferOne!!.position())) { //it could be better here
                    preferOne = item
                }
            }
            if (preferOne != null) {
                availableWriters.remove(preferOne)
            } else if (totalWriters < maxChunkWriters) {
                preferOne = createChunkWriter()
                ++totalWriters
            }
            preferOne?.let {
                busyWriters.add(preferOne!!)
            }
        }
        return preferOne
    }

    fun freeWriter(writer: ChunkWriter) {
        if (busyWriters.remove(writer)) {
            availableWriters.add(writer)
        }
    }

    override fun position(): Long {
//        TODO("Not yet implemented")
        return 0
    }

    private fun createChunkWriter(): ChunkWriter {
        return file!!.let {
            val f = RandomAccessFile(file, "rwd")
            ChunkChannelWriter(f.channel)
        }

    }

    override fun flush() {
        lock.lock {
            if (!closed) {
                for (item in availableWriters) {
                    item.flush()
                }
//                TODO("flush busy writers ?")
//            for (item in busyWriters) {
//                item.flush()
//            }
            } else {
                close()
            }
        }
    }

    override fun close() {
        lock.lock {
            closed = true
            for (item in availableWriters) {
                item.flush()
                item.close()
            }
            for (item in busyWriters) {
                item.flush()
                item.close()
            }
            availableWriters.clear()
            busyWriters.clear()
            totalWriters = 0
        }
    }
}