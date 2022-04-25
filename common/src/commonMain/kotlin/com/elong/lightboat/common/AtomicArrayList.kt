package com.elong.lightboat.common

class AtomicArrayList<T>(private val list: ArrayList<T> = arrayListOf()) {
    private val lock = AtomicLock()

    fun get(index: Int): T {
        return lock.lock {
            list[index]
        }
    }

    fun set(index: Int, element: T): T {
        return lock.lock {
            list.set(index, element)
        }
    }

    fun toList(): List<T> {
        return lock.lock {
            list.toList()
        }
    }

    fun pick(filter: (T) -> Boolean) : T? {
        return lock.lock {
            var value: T? = null
            for (item in list) {
                if(filter(item)) {
                    value = item
                    break
                }
            }
            value
        }
    }
}