package com.github.freeman.bootcamp.database

import java.util.concurrent.CompletableFuture

// TODO: We could create a json file to simulate a permanent database that won't
//       reset when shutting down the app, for testing purpose
class MockDataBase: Database {
    private var hashmap = hashMapOf<String,String>()

    override fun set(key: String, value: String) {
        hashmap[key] = value
    }

    override fun get(key: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        future.complete(hashmap.getOrDefault(key,""))
        return future
    }

}