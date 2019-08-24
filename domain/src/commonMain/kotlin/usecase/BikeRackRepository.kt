package usecase

import repository.Result
import repository.model.BikeRack

interface BikeRackRepository {

    suspend fun getBikeRacks(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Result<List<BikeRack>, Throwable>
}