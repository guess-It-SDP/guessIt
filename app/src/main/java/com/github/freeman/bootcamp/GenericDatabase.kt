package com.github.freeman.bootcamp

interface GenericDatabase {
    fun set(key : String, value: String)
    fun get(key: String): String
}