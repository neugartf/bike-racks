package api.model


import kotlinx.serialization.Serializable


@Serializable
data class Osm3s(
    val timestamp_osm_base: String,
    val copyright: String
)