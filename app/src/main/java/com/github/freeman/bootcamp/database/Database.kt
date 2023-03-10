package com.github.freeman.bootcamp.database

import java.util.concurrent.CompletableFuture

interface Database {
    fun set(key : String, value: String)
    fun get(key: String): CompletableFuture<String>
}