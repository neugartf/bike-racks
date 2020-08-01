package api.model


import kotlinx.serialization.Serializable


@Serializable
data class Elements(
    val type: String,
    val id: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: String? = null,
    val version: Int? = null,
    val changeset: Int? = null,
    val user: String? = null,
    val uid: Int? = null,
    val tags: Tags
)

