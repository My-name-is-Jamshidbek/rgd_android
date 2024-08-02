package com.example.waterfilter.utility

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
open class WebCheckTask : AsyncTask<String, Void, Boolean>() {

    override fun doInBackground(vararg urls: String): Boolean {
        if (urls.isEmpty()) {
            return false // Return false if no URL is provided
        }

        val url = urls[0]

        return try {
            val siteURL = URL(url)
            val connection = siteURL.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            responseCode == 200
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (result) {
            Log.d("WebCheck", "Google.com 200 OK")
        } else {
            Log.d("WebCheck", "Google.com 200 OK not")
        }
    }
}
