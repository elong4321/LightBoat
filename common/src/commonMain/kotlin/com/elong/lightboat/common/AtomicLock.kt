package com.elong.lightboat.common

import java.util.concurrent.atomic.AtomicBoolean

class AtomicLock: AtomicBoolean(false) {
    fun lock() {
        while(compareAndSet(false, true)) ; //just loop for a lock and do nothing
    }

    inline fun <T> lock(block: () -> T): T {
        var value: T
        lock()
        try {
            value = block.invoke()
        } finally {
            unlock()
        }
        return value!! //TODO("deal with !!")
    }

    fun tryLock(): Boolean {
        return compareAndSet(false, true)
    }

    fun unlock() {
        while(compareAndSet(true, false)) ;
    }
}