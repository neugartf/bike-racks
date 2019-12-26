package usecase

import api.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import repository.model.BikeRack


class GetBikeRackByIdUseCase(private val bikeRackRepository: BikeRackRepository) {

    suspend fun execute(
        id: Long
    ): Result<BikeRack, Throwable> {
        return bikeRackRepository.getBikeRackById(id)
    }

    // WTF: suspend is not working on Native
    fun executeiOS(
        id: Long, success: (Result<BikeRack, Throwable>) -> Unit
    ) {
        GlobalScope.launch(ApplicationDispatcher) {
            success(execute(id))
        }
    }
}
