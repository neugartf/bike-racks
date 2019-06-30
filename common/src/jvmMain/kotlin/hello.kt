import api.OverpassApi

suspend fun main() {
    val overpassApi =
        OverpassApi("https://www.overpass-api.de/api/interpreter?data=[out:json];node[amenity=bicycle_parking](43.46669501043081,-5.708215989569187,43.588927989569186,-5.605835010430813);out;")
    print(overpassApi.getBikeRacks())
}