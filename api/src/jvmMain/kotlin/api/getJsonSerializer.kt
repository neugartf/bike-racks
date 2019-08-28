package api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonSerializer
import io.ktor.util.InternalAPI
import java.util.concurrent.TimeUnit

actual fun getJsonSerializer(): JsonSerializer? =
    GsonSerializer()

@UseExperimental(InternalAPI::class)
actual fun getHttpClientEngine(): HttpClientEngine {
    val okHttpConfig = OkHttpConfig()
    okHttpConfig.config {
        readTimeout(10, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)
    }
    return OkHttpEngine(okHttpConfig)
}