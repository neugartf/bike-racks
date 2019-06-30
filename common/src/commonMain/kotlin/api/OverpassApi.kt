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
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.takeFrom

class OverpassApi(private val endPoint: String) {
    private val client = HttpClient {
        install(JsonFeature) {
            defaultSerializer().apply {
                // read(typeInfo<BikeRacksApiModel>(), )
            }

            defaultRequest {
                accept(ContentType.Application.Json)
            }
        }
    }


    val json = io.ktor.client.features.json.defaultSerializer()
    suspend fun getBikeRacks(): BikeRacksApiModel = client.get {
        apiUrl(endPoint)
    }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        url {
            takeFrom(endPoint)
            encodedPath = "/api/interpreter"
        }
    }
}