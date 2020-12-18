package com.example.config

import com.example.beans.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import org.apache.http.HttpHost

const val THREAD_COUNT = 4

// HttpClient with LAN proxy
object HttpClient {

    private val client = HttpClient(Apache) {
        engine {
            threadsCount = THREAD_COUNT
            customizeClient {
                setProxy(HttpHost(
                    AppConfig.proxyConfig.addr,
                    AppConfig.proxyConfig.port,
                    AppConfig.proxyConfig.type))
            }
        }
    }

    suspend fun getStringRequest(urlString : String) = client.get<String>(urlString = urlString)
}
