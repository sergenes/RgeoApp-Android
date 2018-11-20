package com.nes.rgeo.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.nes.rgeo.R
import com.nes.rgeo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    private val LOCATION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        ensurePermissions()
        if (savedInstanceState == null && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment())
                .commitNow()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun ensurePermissions(): Boolean {
        val status = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (!status) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this,
                        "Unable to show location - permission required",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, MainFragment())
                        .commitNow()
                }
            }
        }
    }

}
