package com.elong.lightboat.common

interface Pool<Item> {
    fun <Preference> pick(p: Preference): Item
    fun reuse(item: Item): Boolean
    fun resize(size: Int)
}