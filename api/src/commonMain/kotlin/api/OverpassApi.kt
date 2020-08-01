package api

import api.model.BikeRacksApiModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.cache.HttpCacheEntry
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import io.ktor.util.KtorExperimentalAPI

expect fun getJsonSerializer(): JsonSerializer?

expect fun getHttpClientEngine(): HttpClientEngine

class OverpassApi {

    private val endPoint = "https://overpass.kumi.systems"


    @OptIn(KtorExperimentalAPI::class)
    private val client = HttpClient(getHttpClientEngine()) {
        install(JsonFeature) {
            acceptContentTypes = listOf(ContentType.Application.Json)
            serializer = getJsonSerializer() ?: defaultSerializer()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
    }

    suspend fun getBikeRacks(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Result<BikeRacksApiModel, Throwable> = try {
        val result: BikeRacksApiModel = client.get {
            parameter(
                "data",
                "[out:json];node[amenity=bicycle_parking]($lat1,$lng1,$lat2,$lng2);out 50;"
            )
            apiUrl("/api/interpreter")
        }
        Success(result)
    } catch (e: Exception) {
        Error(e)
    }


    private fun HttpRequestBuilder.apiUrl(path: String) {
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}
// WTF: Arrow just works for Android
sealed class Result<T, U>

data class Success<T, U>(val value: T) : Result<T, U>()

data class Error<T, U>(val value: U) : Result<T, U>()