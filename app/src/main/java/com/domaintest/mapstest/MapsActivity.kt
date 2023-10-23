package com.domaintest.mapstest

import android.content.res.Configuration
import android.location.GpsStatus
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.domaintest.mapstest.databinding.ActivityMapsBinding
import org.osmdroid.api.IMapController
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.graphics.Rect as Rect

class MapsActivity : AppCompatActivity(), MapListener, GpsStatus.Listener {


    private val TAG: String? = "MAP_ACTIVITY"
    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var myLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        org.osmdroid.config.Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        mMap = binding.mapView
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller

        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true
        myLocationOverlay.runOnFirstFix{
            runOnUiThread{
                controller.setCenter(myLocationOverlay.myLocation)
                controller.animateTo(myLocationOverlay.myLocation)
            }
        }

        // val mapPoint = GeoPoint(latitude, longitude)
        controller.setZoom(6.0)
        Log.d(TAG, "onCreate: ${controller.zoomIn()}")
        Log.d(TAG, "onCreate: ${controller.zoomOut()}")

        // controller.animateTo(mapPoint)
        mMap.overlays.add(myLocationOverlay)

        mMap.addMapListener(this)
        
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.d(TAG, "onScroll: ${event?.source?.getMapCenter()?.latitude}")
        Log.d(TAG, "onScroll: ${event?.source?.getMapCenter()?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.d(TAG, "onZoom: ${event?.zoomLevel} source: ${event?.source}")
        return false
    }

    override fun onGpsStatusChanged(p0: Int) {
        TODO("Not yet implemented")
    }


}