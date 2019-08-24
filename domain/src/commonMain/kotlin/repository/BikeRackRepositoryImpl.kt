package repository


import api.OverpassApi
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
        overpassApi.getBikeRacks(lat1, lng1, lat2, lng2).fold({ bikeRacksApiModel ->
            bikeRacksApiModel.elements.map { it.map() }.also {
                it.forEach { bikeRack -> cache[bikeRack.id] = bikeRack }
            }
            return Success(cache.values.toList())
        }, { throwable ->
            return Error(throwable)
        })
}

sealed class Result<T, U>

data class Success<T, U>(val value: T) : Result<T, U>()

data class Error<T, U>(val value: U) : Result<T, U>()



