import api.OverpassApi

suspend fun main() {
    val overpassApi =
        OverpassApi("https://www.overpass-api.de")
    print(overpassApi.getBikeRacks())
}