package usecase

import api.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    fun executeiOS(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double, success: (Result<List<BikeRack>, Throwable>) -> Unit
    ) {
        GlobalScope.launch(ApplicationDispatcher) {
            success(execute(lat1, lng1, lat2, lng2))
        }
    }
}