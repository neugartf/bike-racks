package api.model

import kotlinx.serialization.Serializable


@Serializable
data class Tags(
    val amenity: String,
    val bicycle_parking: String? = null,
    val capacity: String? = null,
    val fee: String? = null,
    val lit: String? = null,
    val opening_hours: String? = null,
    val supervised: String? = null,
    val access: String? = null,
    val covered: String? = null
)