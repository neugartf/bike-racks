package api

import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json

actual fun getJsonSerializer(): JsonSerializer? =
    KotlinxSerializer(Json.nonstrict)
