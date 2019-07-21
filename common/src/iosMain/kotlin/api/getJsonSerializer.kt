package api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonSerializer


actual fun getJsonSerializer(): JsonSerializer? {
    return null
}

actual fun getHttpClientEngine(): HttpClientEngine {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}