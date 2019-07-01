import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import repository.model.BikeRack
import repository.model.Coordinate


@Serializable
data class Elements(
    val type: String,
    val id: Long,
    val lat: Double,
    val lon: Double,
    @Optional
    val timestamp: String? = null,
    @Optional
    val version: Int? = null,
    @Optional
    val changeset: Int? = null,
    @Optional
    val user: String? = null,
    @Optional
    val uid: Int? = null,
    val tags: Tags
)

fun Elements.map() = BikeRack(this.id, Coordinate(this.lat, this.lon), this.tags.capacity?.toInt())