package repository


import api.Error
import api.OverpassApi
import api.Result
import api.Success
import repository.model.BikeRack
import repository.model.map
import usecase.BikeRackRepository

class BikeRackRepositoryImpl(private val overpassApi: OverpassApi) : BikeRackRepository {

    private val cache = hashMapOf<Long, BikeRack>()

    override suspend fun getBikeRacks(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Result<List<BikeRack>, Throwable> =
        when (val result = overpassApi.getBikeRacks(lat1, lng1, lat2, lng2)) {
            is Success -> {
                result.value.elements.map { it.map() }.also {
                    it.forEach { bikeRack -> cache[bikeRack.id] = bikeRack }
                }
                Success(cache.values.toList())
            }
            is Error -> {
                Error(result.value)
            }
        }

}





