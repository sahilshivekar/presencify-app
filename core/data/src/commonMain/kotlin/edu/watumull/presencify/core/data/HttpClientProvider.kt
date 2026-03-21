package edu.watumull.presencify.core.data

import edu.watumull.presencify.core.data.network.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

class HttpClientProvider(
    private val factory: HttpClientFactory,
    private val engine: HttpClientEngine
) {
    private var client: HttpClient = factory.create(engine)

    fun getClient(): HttpClient = client

    fun recreateClient() {
        println("♻️ Recreating HttpClient")
        client.close()
        client = factory.create(engine)
    }
}