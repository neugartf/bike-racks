package usecase

import repository.Result
import repository.model.BikeRack


class GetBikeRacksUseCase(private val bikeRackRepository: BikeRackRepository) {

    suspend fun execute(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Result<List<BikeRack>, Throwable> {
        return bikeRackRepository.getBikeRacks(lat1, lng1, lat2, lng2)
    }
}