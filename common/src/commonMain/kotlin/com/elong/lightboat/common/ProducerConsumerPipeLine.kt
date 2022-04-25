package com.elong.lightboat.common

import java.io.InterruptedIOException
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.NoSuchElementException

class ProducerConsumerPipeLine<Item>: AbstractQueue<Item>, BlockingQueue<Item> {
    private var items: Array<Any?>
    private var takeIndex: Int = 0
    private var putIndex: Int = 0
    private var count: Int = 0

    private var lock: ReentrantLock
    private var notEmpty: Condition
    private var notFull: Condition

    private var closed = false

    private var itrs: Itrs? = null

    constructor(capacity: Int) : this(capacity, false)

    constructor(capacity: Int, fair: Boolean) {
        if (capacity <= 0) {
            throw IllegalArgumentException()
        }
        items = Array(capacity) { null }
        lock = ReentrantLock(fair)
        notEmpty = lock.newCondition()
        notFull = lock.newCondition()
    }

    private fun itemAt(index: Int): Item {
        return items[index] as Item
    }

    override fun iterator(): MutableIterator<Item> {
        TODO("Not yet implemented")
    }

    private fun enqueue(e: Item) {
        val items = this.items
        items[putIndex] = e
        if (++putIndex == items.size) {
            putIndex = 0
        }
        ++count
        notEmpty.signal()
    }

    private fun dequeue(): Item {
        val items = this.items
        val x = items[takeIndex] as Item
        items[takeIndex] = null
        if (++takeIndex == items.size) {
            takeIndex = 0
        }
        --count
        if (itrs != null) {

        }
        notFull.signal()
        return x
    }

    private fun checkClosed() {
        if (closed) {
            notEmpty.signalAll()
            notFull.signalAll()
            throw InterruptedException()
        }
    }

    public fun isShutDown(): Boolean {
        val lock = this.lock
        lock.lock()
        try {
            return closed
        } finally {
            lock.unlock()
        }
    }

    public fun shutDown() {
        val lock = this.lock
        lock.lock()
        closed = true
        lock.unlock()
    }

    override fun offer(e: Item): Boolean {
        checkNotNull(e)
        val lock = this.lock
        lock.lock()
        try {
            checkClosed()
            if (count == items.size) {
                return false
            } else {
                enqueue(e)
                return true
            }
        } finally {
            lock.unlock()
        }
    }

    override fun offer(item: Item, timeout: Long, unit: TimeUnit?): Boolean {
        checkNotNull(item)
        var nanos = unit?.toNanos(timeout) ?: timeout
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            checkClosed()
            while (count == items.size) {
                if (nanos < 0) {
                    return false
                }
                nanos = notFull.awaitNanos(nanos)
            }
            enqueue(item)
            return true
        } finally {
            lock.unlock()
        }
    }

    override fun poll(): Item? {
        val lock = this.lock
        lock.lock()
        try {
            checkClosed()
            return if (count == 0) null else dequeue()
        } finally {
            lock.unlock()
        }
    }

    override fun poll(timeout: Long, unit: TimeUnit?): Item? {
        var nanos = unit!!.toNanos(timeout)
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            checkClosed()
            while (count == 0) {
                if (nanos < 0) {
                    return null
                }
                nanos = notEmpty.awaitNanos(nanos)
            }
            return dequeue()
        } finally {
            lock.unlock()
        }
    }

    override fun peek(): Item? {
        val lock = this.lock
        lock.lock()
        try {
            checkClosed()
            return itemAt(takeIndex)
        } finally {
            lock.unlock()
        }
    }

    override val size: Int
        get() {
            val lock = this.lock
            try {
                return count
            } finally {
                lock.unlock()
            }
        }

    override fun put(item: Item) {
        checkNotNull(item)
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            checkClosed()
            while (count == items.size) {
                notFull.await()
            }
            enqueue(item)
        } finally {
            lock.unlock()
        }
    }

    override fun take(): Item {
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            checkClosed()
            while (count == 0) {
                notEmpty.await()
            }
            return dequeue()
        } finally {
            lock.unlock()
        }
    }

    override fun remainingCapacity(): Int {
        val lock = this.lock
        lock.lock()
        try {
            return (items.size - count)
        } finally {
            lock.unlock()
        }
    }

    override fun drainTo(c: MutableCollection<in Item>?): Int {
        return drainTo(c, Int.MAX_VALUE)
    }

    override fun drainTo(c: MutableCollection<in Item>?, maxElements: Int): Int {
        checkNotNull(c)
        if (c == this) {
            throw java.lang.IllegalArgumentException()
        }
        if (maxElements <= 0) {
            return 0
        }
        val items = this.items
        val lock = this.lock
        lock.lock()
        try {
            val n = minOf(maxElements, count)
            var take = takeIndex
            var i = 0
            try {
                while (i < n) {
                    val e = items[take]
                    c.add(e as Item)
                    items[take] = null
                    if (++take == items.size) {
                        take = 0
                    }
                    ++i
                }
                return n
            } finally {
                if (i > 0) {
                    count -= i
                    takeIndex = take
                    if (itrs != null) {
                        //TODO: do something with itrs
                    }
                }
                while (i-- > 0 && lock.hasWaiters(notFull)) {
                    notFull.signal()
                }
            }
        } finally {
            lock.unlock()
        }
    }

    private inner class Itrs {

        inner class Node(referent: Itr?, var next: Node?) : WeakReference<Itr>(referent);

        var cycle = 0
        private var head: Node? = null
        private var sweeper: Node? = null

        private val SHORT_SWEEP_PROBES = 4
        private val LONG_SWEEP_PROBES = 16

        constructor(initial: Itr) {
            register(initial)
        }

        fun register(itr: Itr) {
            head = Node(itr, head)
        }

        fun doSomeSweeping(tryHarder: Boolean) {
            var probes = if (tryHarder) LONG_SWEEP_PROBES else SHORT_SWEEP_PROBES
            var o: Node?; var p: Node?
            val sweeper = this.sweeper
            var passedGo = false

            if (sweeper == null) {
                o = null
                p = head
                passedGo = true
            } else {
                o = sweeper
                p = sweeper.next
                passedGo = false
            }

            while (probes-- > 0) {
                if (p == null) {
                    if (passedGo) {
                        break
                    }
                    o = null
                    p = head
                    passedGo = true
                }
                val it = p!!.get()
                val next = p!!.next
                if (it == null || it.isDetached()) {
                    probes = LONG_SWEEP_PROBES
                    p.clear()
                    p.next = null
                    if (o == null) {
                        head = next
                        if (next == null) {
                            itrs = null
                            return
                        }
                    } else {
                        o.next = next
                    }
                } else {
                    o = p
                }
                p = next
            }
            this.sweeper = if (p == null) null else o
        }
    }

    private inner class Itr: Iterator<Item> {
        private var cursor: Int = 0
        private var nextItem: Item? = null
        private var nextIndex: Int = 0
        private var lastItem: Item? = null
        private var lastRet: Int = 0
        private var prevTakeIndex = 0
        private var prevCycles = 0

        private val NONE = -1
        private val REMOVED = -2
        private val DETACHED = -3

        constructor() {
            lastRet = NONE
            val lock = this@ProducerConsumerPipeLine.lock
            lock.lock()
            try {
                if (count == 0) {
                    cursor = NONE
                    nextIndex = NONE
                    prevTakeIndex = NONE
                } else {
                    val takeIndex = this@ProducerConsumerPipeLine.takeIndex
                    prevTakeIndex = takeIndex
                    nextIndex = takeIndex
                    nextItem = itemAt(nextIndex)
                    cursor = incCursor(takeIndex)
                }
                if (itrs == null) {
                    itrs = Itrs(this)
                } else {
                    itrs!!.register(this)
                    itrs!!.doSomeSweeping(false)
                }
                prevCycles = itrs!!.cycle
            } finally {
                lock.unlock()
            }
        }

        fun isDetached(): Boolean {
            return prevTakeIndex < 0
        }

        private fun incCursor(index: Int): Int {
            var i = index
            if (++i == this@ProducerConsumerPipeLine.items.size) {
                i = 0
            }
            if (i == putIndex) {
                i = NONE
            }
            return i
        }

        private fun invalidated(index: Int, prevTakeIndex: Int, dequeues: Long, length: Int): Boolean {
            if (index < 0) {
                return false
            }
            var distance = index - prevTakeIndex
            if (distance < 0) {
                distance += length
            }
            return dequeues > distance
        }

        private fun detach() {
            if (prevTakeIndex >= 0) {
                prevTakeIndex = DETACHED
                this@ProducerConsumerPipeLine.itrs!!.doSomeSweeping(true)
            }
        }

        override fun hasNext(): Boolean {
            if (nextItem != null) {
                return true
            }
            noNext()
            return false
        }

        private fun noNext() {
            val lock = this@ProducerConsumerPipeLine.lock
            lock.lock()
            try {
                if (!isDetached()) {
                    incorporateDequeues()
                    if (lastRet >= 0) {
                        lastItem = itemAt(lastRet)
                        detach()
                    }
                }
            } finally {
                lock.unlock()
            }
        }

        override fun next(): Item {
            var x = nextItem
            if (x == null) {
                throw NoSuchElementException()
            }
            val lock = this@ProducerConsumerPipeLine.lock
            lock.lock()
            try {
                if (!isDetached()) {
                    incorporateDequeues()
                    lastRet = nextIndex
                    val cursor = this.cursor
                    if (cursor > 0) {
                        nextIndex = cursor
                        nextItem = itemAt(nextIndex)
                        this.cursor = incCursor(cursor)
                    } else {
                        nextIndex = NONE
                        nextItem = null
                    }
                }
            } finally {
                lock.unlock()
            }
            return x
        }

        fun incorporateDequeues() {
            TODO("Not yet implemented")
        }

    }
}