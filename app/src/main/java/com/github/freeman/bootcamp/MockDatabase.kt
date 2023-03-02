package com.github.freeman.bootcamp

class MockDatabase: GenericDatabase {
    var hashmap = hashMapOf<String,String>()

    override fun set(key: String, value: String) {
        hashmap.put(key, value )
    }

    override fun get(key: String): String {
        return hashmap.getOrDefault(key,"")
    }

}