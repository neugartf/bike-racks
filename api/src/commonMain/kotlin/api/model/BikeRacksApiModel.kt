package api.model


import kotlinx.serialization.*

@Serializable
data class BikeRacksApiModel(
    val version: Double,
    val generator: String,
    val osm3s: Osm3s,
    val elements: List<Elements>
)