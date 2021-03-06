package view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import api.OverpassApi
import api.Result
import api.Success
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonPrimitive
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import repository.BikeRackRepositoryImpl
import repository.model.BikeRack
import usecase.GetBikeRackByIdUseCase
import usecase.GetBikeRacksUseCase
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener, CoroutineScope {

    private var job: Job = Job()

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapBoxMap: MapboxMap
    val repositoryImpl = BikeRackRepositoryImpl(OverpassApi())

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Mapbox.getInstance(
            this, API_TOKEN
        )

        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            mapBoxMap.locationComponent.lastKnownLocation?.let {
                mapBoxMap.easeCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitude, it.longitude), ZOOM_LEVEL
                    ), object : MapboxMap.CancelableCallback {
                        override fun onCancel() {
                            // NOP
                        }

                        override fun onFinish() {
                            updateVisibleArea()
                        }
                    }
                )
            }
        }

        button.setOnClickListener {
            updateVisibleArea()
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun updateVisibleArea() {
        mapBoxMap.projection.visibleRegion.latLngBounds?.let {
            launch {
                when (val result = getRacks(it)) {
                    is Success -> {
                        showRacks(result.value)
                    }
                    is Error -> {
                        showError(result as api.Error<List<BikeRack>, Throwable>)
                    }
                }
            }
        }
    }

    private fun showError(result: api.Error<List<BikeRack>, Throwable>) {
        Crashlytics.logException(result.value)
        Toast.makeText(this@MainActivity, result.value.localizedMessage, Toast.LENGTH_LONG).show()
    }

    private fun showRacks(racks: List<BikeRack>) {
        val symbolLayerIconFeatureList = mutableListOf<SymbolOptions>()
        racks.forEach { bikeRack ->
            val coordinate = bikeRack.coordinate
            val symbolOptions = SymbolOptions().withGeometry(
                Point.fromLngLat(
                    coordinate.lng,
                    coordinate.lat
                )
            ).withIconImage("marker-icon-id").withData(JsonPrimitive(bikeRack.id))

            symbolLayerIconFeatureList.add(
                symbolOptions
            )
        }
        mapBoxMap.setStyle(Style.LIGHT) { style ->

            val symbolManager = SymbolManager(mapView, mapBoxMap, style)

            symbolManager.create(symbolLayerIconFeatureList)
            style.addImage(
                "marker-icon-id",
                BitmapFactory.decodeResource(
                    this@MainActivity.resources,
                    R.drawable.mapbox_marker_icon_default
                )
            )

            symbolManager.addClickListener {
                rackInfoCard.visibility = View.VISIBLE
                val id = it.data?.asJsonPrimitive?.asLong ?: 0
                launch {
                    withContext(Dispatchers.IO) {
                        when (val result = GetBikeRackByIdUseCase(repositoryImpl).execute(id)) {
                            is Success ->
                                withContext(Dispatchers.Main) {
                                    if (result.value.capacity != null) {
                                        places.text =
                                            getString(
                                                R.string.capacity_information,
                                                result.value.capacity
                                            )
                                    } else {
                                        places.text = getString(R.string.no_capacity_information)
                                    }
                                }
                            is api.Error ->
                                withContext(Dispatchers.Main) {
                                    places.text = getString(R.string.no_capacity_information)
                                }
                        }
                    }
                }
            }
        }
    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        mapBoxMap = mapboxMap
        mapBoxMap.setStyle(Style.LIGHT) {
            enableLocationComponent(it)
        }

    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapbox_location_layer_blue))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapBoxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                zoomWhileTracking(ZOOM_LEVEL, 750, object : MapboxMap.CancelableCallback {
                    override fun onFinish() {
                        updateVisibleArea()
                    }

                    override fun onCancel() {
                        // NOP
                    }
                })

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "User location explanation", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapBoxMap.style!!)
        } else {
            Toast.makeText(this, "User location not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun getRacks(latLngBounds: LatLngBounds): Result<List<BikeRack>, Throwable> =
        withContext(Dispatchers.IO) {
            GetBikeRacksUseCase(repositoryImpl).execute(
                latLngBounds.latSouth,
                latLngBounds.lonWest,
                latLngBounds.latNorth,
                latLngBounds.lonEast
            )
        }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {
        private const val ZOOM_LEVEL = 16.0
        private const val API_TOKEN =
            "pk.eyJ1IjoibmV1Z2FydGYiLCJhIjoiY2pkMjZobHhoMmZieTJ3czZlb2JmczIyaSJ9.N0ziUNQ4Dvmskp-WbmmXrA"
    }
}

