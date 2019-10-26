package api

import api.model.BikeRacksApiModel
import api.model.Elements
import api.model.Osm3s
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer


actual fun getJsonSerializer(): JsonSerializer? {
    // WTF: Native doesnt support reflection for serialization
    return KotlinxSerializer().apply {
        setMapper(BikeRacksApiModel::class, BikeRacksApiModel.serializer())
        setMapper(Osm3s::class, Osm3s.serializer())
        setListMapper(Elements::class, Elements.serializer())
    }
}

actual fun getHttpClientEngine(): HttpClientEngine {
    return Ios.create {
        this.configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}