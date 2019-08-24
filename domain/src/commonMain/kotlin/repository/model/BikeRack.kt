package repository.model

import api.model.Elements

data class BikeRack(val id: Long, val coordinate: Coordinate, val capacity: Int?)

fun Elements.map() = BikeRack(
    this.id, Coordinate(this.lat, this.lon), try {
        this.tags.capacity?.toInt()
    } catch (e: NumberFormatException) {
        null
    }
)