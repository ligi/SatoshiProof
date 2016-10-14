package org.ligi.satoshiproof.util

import okhttp3.OkHttpClient
import okhttp3.Request

object Downloader {
    fun downloadURL(url : String) : String? {
        return try {

            val client = OkHttpClient()

            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            response.body().string()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
}
