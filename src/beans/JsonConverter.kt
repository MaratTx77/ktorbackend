package com.example.config

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException


object JsonConverter {
    private val mapper by lazy { ObjectMapper()}

    @Throws(IOException::class)
    fun <T> toObject(type: Class<T>?, json: String?): T = mapper.readValue(json, type)

    @Throws(IOException::class)
    fun toJsonFormattedString(value: Any): String = mapper.writeValueAsString(value)

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    fun toMap(json: String): Map<String, String> = mapper.readValue(json, Map::class.java) as Map<String, String>

}
