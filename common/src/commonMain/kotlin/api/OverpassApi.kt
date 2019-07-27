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

expect fun getJsonSerializer(): JsonSerializer?

expect fun getHttpClientEngine(): HttpClientEngine

class OverpassApi {

    private val endPoint = "http://www.overpass-api.de"


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

    suspend fun getBikeRacks(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Result<BikeRacksApiModel> {
        try {
            val result: BikeRacksApiModel = client.get {
                parameter(
                    "data",
                    "[out:json];node[amenity=bicycle_parking]($lat1,$lng1,$lat2,$lng2);out 50;"
                    //"[out:json];node[amenity=bicycle_parking](41.39774567270604,2.2073610319612555,41.413251623343285,2.2203049944921247);out 50;"
                )
                apiUrl("/api/interpreter")
            }
            return Result.success(result)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}