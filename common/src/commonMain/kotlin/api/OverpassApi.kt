package api

import api.model.BikeRacksApiModel
import io.ktor.client.HttpClient
import io.ktor.client.call.TypeInfo
import io.ktor.client.call.typeInfo
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.Json.Companion.nonstrict
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.json

expect fun getJsonSerializer(): JsonSerializer?

class OverpassApi {

    private val endPoint = "https://www.overpass-api.de"

    private val client = HttpClient {
        install(JsonFeature) {
            acceptContentTypes = listOf(ContentType.Application.Json)
            serializer = getJsonSerializer() ?: defaultSerializer()
        }
    }

    suspend fun getBikeRacks(lat1: Double, lng1: Double, lat2: Double, lng2: Double): BikeRacksApiModel = client.get {
        parameter(
            "data",
            "[out:json];node[amenity=bicycle_parking]($lat1,$lng1,$lat2,$lng2);out;"
        )
        apiUrl("/api/interpreter")
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}