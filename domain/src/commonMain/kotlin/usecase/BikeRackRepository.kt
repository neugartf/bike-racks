package usecase

import api.Result
import repository.model.BikeRack

interface BikeRackRepository {

    suspend fun getBikeRacks(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Result<List<BikeRack>, Throwable>

    suspend fun getBikeRackById(id: Long): Result<BikeRack, Throwable>
}
