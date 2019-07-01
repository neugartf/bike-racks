import api.OverpassApi
import repository.BikeRackRepository

suspend fun main() {
    print(
        BikeRackRepository(OverpassApi()).getBikeRacks(
            43.46669501043081,
            -5.708215989569187,
            43.588927989569186,
            -5.605835010430813
        )
    )
}