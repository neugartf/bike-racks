package api.model

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable


@Serializable
data class Tags(
    val amenity: String,
    @Optional
    val bicycle_parking: String? = null,
    @Optional
    val capacity: String? = null,
    @Optional
    val fee: String? = null,
    @Optional
    val lit: String? = null,
    @Optional
    val opening_hours: String? = null,
    @Optional
    val supervised: String? = null,
    @Optional
    val access: String? = null,
    @Optional
    val covered: String? = null
)