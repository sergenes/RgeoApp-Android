package com.nes.rgeo.ui.main

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.nes.rgeo.R
import com.google.android.gms.maps.MapsInitializer

import kotlinx.android.synthetic.main.new_main_fragment.*

class MainFragment : androidx.fragment.app.Fragment() {
    private val TAG = this@MainFragment.javaClass.simpleName

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.new_main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mapMapView.onCreate(savedInstanceState)

        //get stored app state from the Shared Preferences
        viewModel.loadState(this)
        viewModel.restoredLog?.let {
            outputEditText.append(it)
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        //subscribe for changes in addressLog
        viewModel.addressLog.observe(this, Observer {
            it?.let { value ->
                outputEditText.append("* $value\n")
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })

        //subscribe for changes in requestsCount
        viewModel.requestsCount.observe(this, Observer {
            it?.let { count ->
                requestsEditText.text.clear()
                requestsEditText.text.append("$count")
            }
        })

        //subscribe for changes in errorCode
        viewModel.errorCode.observe(this, Observer {
            val builder = AlertDialog.Builder(this@MainFragment.activity)

            builder.setTitle(getString(R.string.error_network_title))
            builder.setMessage(getString(R.string.error_network_message))
            builder.setPositiveButton(getString(R.string.error_network_button)) { dialog, which ->

            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        })

        mapMapView.getMapAsync {
            Log.d(TAG, "getMapAsync")
            viewModel.initGoogleMap(it, this.activity!!)

            MapsInitializer.initialize(this.activity)
        }

        getCivicButton.setOnClickListener {
            viewModel.addRequest()
        }
    }

    override fun onPause() {
        super.onPause()
        mapMapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapMapView.onStart()
    }

    override fun onStop() {
        // save the app state in Shared Preferences
        viewModel.saveState(this)

        super.onStop()
        mapMapView.onStop()
    }
}
