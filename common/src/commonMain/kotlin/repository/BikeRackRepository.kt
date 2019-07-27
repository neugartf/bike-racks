package repository

import api.OverpassApi
import map
import repository.model.BikeRack

class BikeRackRepository(private val overpassApi: OverpassApi) {

    private val cache = hashMapOf<Long, BikeRack>()

    suspend fun getBikeRacks(lat1: Double, lng1: Double, lat2: Double, lng2: Double): List<BikeRack> =
        overpassApi.getBikeRacks(lat1, lng1, lat2, lng2).fold({ bikeRacksApiModel ->
            bikeRacksApiModel.elements.map { it.map() }.also {
                it.forEach { bikeRack -> cache[bikeRack.id] = bikeRack }
            }
            return cache.values.toList()
        },
            { return emptyList() }
        )
}



