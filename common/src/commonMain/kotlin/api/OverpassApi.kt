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

class OverpassApi(private val endPoint: String) {

    private val client = HttpClient {
        install(JsonFeature) {
            acceptContentTypes = listOf(ContentType.Application.Json)
            serializer = getJsonSerializer() ?: defaultSerializer()
        }
    }

    suspend fun getBikeRacks(): BikeRacksApiModel = client.get {
        parameter(
            "data",
            "[out:json];node[amenity=bicycle_parking](43.46669501043081,-5.708215989569187,43.588927989569186,-5.605835010430813);out;"
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