package api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json

actual fun getJsonSerializer(): JsonSerializer? =
    GsonSerializer()

@UseExperimental(InternalAPI::class)
actual fun getHttpClientEngine(): HttpClientEngine  = OkHttpEngine(OkHttpConfig())