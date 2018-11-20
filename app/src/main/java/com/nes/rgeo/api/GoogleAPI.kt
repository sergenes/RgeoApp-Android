package com.nes.rgeo.api

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.nes.rgeo.BuildConfig
import okhttp3.*
import java.io.IOException
import com.nes.rgeo.model.StreetAddress
import com.nes.rgeo.tools.XMLParser
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


interface GoogleAPIProtocol {
    fun updateRequestsCount(count: Int)
    fun updateResultLog(result: StreetAddress)
    fun showNetworkError(code: APIError)
}

enum class APIError {
    NetworkError, ParserError, UnknownError
}

class GoogleAPI private constructor(private var context: Context) {
    private val API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=%s,%s&key=%s"

    private lateinit var client: OkHttpClient
    private val xmlParser = XMLParser()

    private val lock = Object()

    var delegate: GoogleAPIProtocol? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var singleton: GoogleAPI? = null

        fun singleton(): GoogleAPI {
            return if (singleton == null) {
                throw IllegalStateException("Must Initialize GoogleAPI before using singleton()")
            } else {
                singleton!!
            }
        }

        // call this method once, on app.create
        // we need the context to access request template from the strings
        fun with(context: Context): GoogleAPI {
            if (singleton == null) {
                val var1 = GoogleAPI::class.java
                synchronized(var1) {
                    if (singleton == null) {
                        singleton = GoogleAPI(context)
                        singleton?.init()
                    }
                }
            }
            return singleton!!
        }
    }

    private fun init() {
        client = OkHttpClient.Builder().build()
    }

    fun addRequest(request: LatLng) {
        synchronized(lock) {
            jobExecutor.execute(JobRunnable(request))
            delegate?.updateRequestsCount(jobExecutor.getQueueSize())
        }
    }

    private fun doReverseGeocode(location: LatLng) {
        delegate?.updateRequestsCount(jobExecutor.getQueueSize())

        val url = API_BASE_URL.format(location.latitude, location.longitude, BuildConfig.ApiKey)

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "text/xml")
            .build()

        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    delegate?.showNetworkError(APIError.NetworkError)
                }

                override fun onResponse(call: Call, response: Response) {
                    val strResult = response.body()?.string()
                    try {
                        val res = xmlParser.parse(strResult!!)
                        val objectAddress = StreetAddress(res)
                        delegate?.updateResultLog(objectAddress)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        delegate?.showNetworkError(APIError.ParserError)
                    }
                }

            })
        } catch (e: Exception) {
            delegate?.showNetworkError(APIError.UnknownError)
        }

    }

    internal inner class JobRunnable(val location: LatLng) : Runnable {
        override fun run() {
            doReverseGeocode(location)
            // need to delay a request to the server
            // otherwise, the changes of the Number Outstanding Requests field
            // may not be visible by eye (Server responds very fast)
            Thread.sleep(1000)
        }
    }

    private val jobExecutor = SerialExecutor(Executors.newSingleThreadExecutor())

    internal inner class SerialExecutor(private val executor: Executor) : Executor {
        private val tasks: Queue<Runnable> = ArrayDeque()
        private var active: Runnable? = null

        @Synchronized
        fun getQueueSize(): Int {
            return tasks.size
        }

        @Synchronized
        override fun execute(r: Runnable) {
            tasks.offer(Runnable {
                try {
                    r.run()
                } finally {
                    scheduleNext()
                }
            })
            if (active == null) {
                scheduleNext()
            }
        }

        @Synchronized
        private fun scheduleNext() {
            active = tasks.poll()
            active?.let {
                executor.execute(active)
            }
        }
    }
}