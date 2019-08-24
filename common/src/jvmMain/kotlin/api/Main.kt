package api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repository.BikeRackRepositoryImpl

fun main(args: Array<String>) {
    for (i in 1..20) {
        GlobalScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.IO) {
                BikeRackRepositoryImpl(OverpassApi()).getBikeRacks(
                    43.46669501043081,
                    -5.708215989569187,
                    43.588927989569186,
                    -5.605835010430813
                )
            }
        }
    }
    Thread.sleep(10000)
}
