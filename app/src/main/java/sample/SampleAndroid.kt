package sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import api.OverpassApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repository.BikeRackRepository

actual class Sample {
    actual fun checkMe() = 44
}

actual object Platform {
    actual val name: String = "Android"
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hello()
        Sample().checkMe()
        setContentView(R.layout.activity_main)


    }

    override fun onPostResume() {
        super.onPostResume()

        GlobalScope.launch(Dispatchers.Main) {
            racks.text = getRacks().toString()
        }
    }

    private suspend fun getRacks() =
        withContext(Dispatchers.IO) {
            BikeRackRepository(OverpassApi()).getBikeRacks(
                43.46669501043081,
                -5.708215989569187,
                43.588927989569186,
                -5.605835010430813
            )

        }
}

