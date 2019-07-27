package sample

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import api.OverpassApi
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import repository.BikeRackRepository
import repository.model.BikeRack
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener, CoroutineScope {

    private var job: Job = Job()

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapBoxMap: MapboxMap

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(
            this,
            "pk.eyJ1IjoibmV1Z2FydGYiLCJhIjoiY2pkMjZobHhoMmZieTJ3czZlb2JmczIyaSJ9.N0ziUNQ4Dvmskp-WbmmXrA"
        )

        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            mapBoxMap.locationComponent.lastKnownLocation?.let {
                mapBoxMap.easeCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude, it.longitude
                        ), 16.0
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
                val symbolLayerIconFeatureList = mutableListOf<Feature>()
                val racks = getRacks(it)
                if (racks.isNotEmpty()) {
                    racks.forEach { bikeRack ->
                        val coordinate = bikeRack.coordinate
                        symbolLayerIconFeatureList.add(
                            Feature.fromGeometry(
                                Point.fromLngLat(
                                    coordinate.lng,
                                    coordinate.lat
                                )
                            )
                        )
                    }.also {
                        mapBoxMap.setStyle(Style.LIGHT) { style ->
                            style.addImage(
                                "marker-icon-id",
                                BitmapFactory.decodeResource(
                                    this@MainActivity.resources, R.drawable.mapbox_marker_icon_default
                                )
                            )
                            val geoJsonSource = GeoJsonSource(
                                "source-id", FeatureCollection.fromFeatures(symbolLayerIconFeatureList)
                            )
                            style.addSource(geoJsonSource)

                            val symbolLayer = SymbolLayer("layer-id", "source-id")
                            symbolLayer.withProperties(
                                PropertyFactory.iconImage("marker-icon-id"), PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconOffset(
                                    arrayOf(0f, -9f)
                                )
                            )
                            style.addLayer(symbolLayer)
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "No racks found", Toast.LENGTH_LONG).show()

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

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
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

                zoomWhileTracking(16.0, 750, object : MapboxMap.CancelableCallback {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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

    private suspend fun getRacks(latLngBounds: LatLngBounds): List<BikeRack> =
        withContext(Dispatchers.IO) {
            BikeRackRepository(OverpassApi()).getBikeRacks(
                latLngBounds.latSouth,
                latLngBounds.lonWest,
                latLngBounds.latNorth,
                latLngBounds.lonEast
            )
        }
}

