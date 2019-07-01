package repository

import api.OverpassApi
import repository.model.BikeRack
import map

class BikeRackRepository(private val overpassApi: OverpassApi) {

    private val cache = hashMapOf<Long, BikeRack>()

    suspend fun getBikeRacks(lat1: Double, lng1: Double, lat2: Double, lng2: Double): List<BikeRack> {
        overpassApi.getBikeRacks(lat1, lng1, lat2, lng2).elements.map { it.map() }.also {
            cache.clear()
            it.forEach { bikeRack -> cache[bikeRack.id] = bikeRack }
        }
        return cache.values.toList()
    }
}


