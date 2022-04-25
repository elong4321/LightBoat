package com.elong.lightboat.download

import com.elong.lightboat.common.Pool
import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue

class BufferPool(var capacity: Int): Pool<ByteBuffer> {
    private val queue = ArrayBlockingQueue<ByteBuffer>(capacity)
    override fun <Preference> pick(p: Preference): ByteBuffer {
        var item = queue.poll()
        if (item == null) {
            if (queue.size < capacity) {
                item = ByteBuffer.allocate(Config.bufferSize)
            } else {
                item = queue.take()
            }
        }
        return item
    }

    override fun reuse(item: ByteBuffer): Boolean {
        item.clear()
        if (queue.size < capacity) {
            queue.put(item)
        }
        return true
    }



    override fun resize(size: Int) {
        TODO("Not yet implemented")
    }
}