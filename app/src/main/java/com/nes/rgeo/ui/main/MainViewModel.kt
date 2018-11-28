package com.nes.rgeo.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.nes.rgeo.api.*
import com.nes.rgeo.model.StreetAddress
import kotlinx.android.synthetic.main.new_main_fragment.*
import java.lang.StringBuilder

class MainViewModel : ViewModel(), GoogleAPIProtocol {
    private val TAG = this@MainViewModel.javaClass.simpleName

    private val PREFS_FILENAME = "com.nes.rgeo.ui.main.prefs"

    // default value
    private val DEFAULT_CENTER = LatLng(42.3165894,-71.0525886)
    private val DEFAULT_ZOOM = 15f

    private lateinit var googleMap: GoogleMap
    var addressLog: MutableLiveData<String?> = MutableLiveData()
    var requestsCount: MutableLiveData<Int> = MutableLiveData()
    var errorCode: MutableLiveData<APIError> = MutableLiveData()
    var restoredLog: String? = null
    private var mapCenter = DEFAULT_CENTER
    private var mapZoom: Float = DEFAULT_ZOOM

    init {
        GoogleAPI.singleton().delegate = this
    }

    fun initGoogleMap(map: GoogleMap, context:Context) {
        googleMap = map

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isCompassEnabled = true

        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            googleMap.isMyLocationEnabled = true
        }

        // use latest saved map center and zoom, or use defaults
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, mapZoom))
        googleMap.setOnCameraIdleListener {
            Log.d(TAG, "cameraPosition=>${googleMap.cameraPosition.target}")
        }
    }

    fun addRequest() {
        GoogleAPI.singleton().addRequest(googleMap.cameraPosition.target)
    }

    fun loadState(fragment: MainFragment) {
        fragment.activity?.let { activity ->
            val prefs = activity.getSharedPreferences(PREFS_FILENAME, 0)
            val latitude = prefs.getFloat("latitude", DEFAULT_CENTER.latitude.toFloat())
            val longitude = prefs.getFloat("longitude", DEFAULT_CENTER.longitude.toFloat())
            mapCenter = LatLng(latitude.toDouble(), longitude.toDouble())
            mapZoom = prefs.getFloat("mapZoom", DEFAULT_ZOOM)
            restoredLog = prefs.getString("addressLog", null)
            //format restored string to show each address on a new line
            restoredLog?.let {
                val list = it.split("*")
                val stringBuilder = StringBuilder()
                list.forEach { item->
                    if (item.trim().isNotBlank()){
                        stringBuilder.append("* ${item.trim()}\n")
                    }
                }
                restoredLog = stringBuilder.toString()
            }
        }
    }

    fun saveState(fragment: MainFragment) {
        fragment.activity?.let { context ->
            val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            prefs?.let {
                val editor = it.edit()
                editor.putString("addressLog", fragment.outputEditText.text.toString().trim())
                editor.putFloat("mapZoom", googleMap.cameraPosition.zoom)
                editor.putFloat("latitude", googleMap.cameraPosition.target.latitude.toFloat())
                editor.putFloat("longitude", googleMap.cameraPosition.target.longitude.toFloat())
                editor.apply()
            }
        }
    }


    //GoogleAPIProtocol callbacks
    override fun updateRequestsCount(count: Int) {
        requestsCount.postValue(count)
    }

    override fun updateResultLog(result: StreetAddress) {
//        addressLog.postValue("${result.streetNumber} ${result.addressLine}, ${result.city}, ${result.stateCode}")
        addressLog.postValue("${result.formattedAddress}")
    }

    override fun showNetworkError(code: APIError) {
        errorCode.postValue(code)
    }
}
