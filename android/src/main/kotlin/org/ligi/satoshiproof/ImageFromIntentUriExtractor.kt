package org.ligi.satoshiproof

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

class ImageFromIntentUriExtractor(private val context: Context) {

    fun extract(selectedImage: Uri?): File? {
        var selectedImage = selectedImage
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = context.contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
        // some devices (OS versions return an URI of com.android instead of com.google.android
        if (selectedImage.toString().startsWith("content://com.android.gallery3d.provider")) {
            // use the com.google provider, not the com.android provider.
            selectedImage = Uri.parse(selectedImage.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"))
        }
        if (cursor != null) {
            cursor.moveToFirst()
            var columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            // if it is a picasa image on newer devices with OS 3.0 and up
            if (selectedImage!!.toString().startsWith("content://com.google.android.gallery3d")) {
                columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    val uriurl = selectedImage
                    // Do this in a background thread, since we are fetching a large image from the web

                    return getBitmap("image_file_name.jpg", uriurl)

                }
            } else { // it is a regular local image file
                val filePath = cursor.getString(columnIndex)
                cursor.close()
                return File(filePath)
            }
        } else if (selectedImage != null && selectedImage.toString().length > 0) {
            val uriurl = selectedImage
            // Do this in a background thread, since we are fetching a large image from the web
            return getBitmap("image_file_name.jpg", uriurl)
        }// If it is a picasa image on devices running OS prior to 3.0
        return null
    }


    private fun getBitmap(tag: String, url: Uri): File? {
        val cacheDir = context.cacheDir

        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val f = File(cacheDir, tag)

        try {
            f.outputStream().use { getInputStreamByURL(url).copyTo(it) }
            return f
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

    }

    @Throws(IOException::class)
    private fun getInputStreamByURL(url: Uri): InputStream {
        if (url.toString().startsWith("content://com.google.android.gallery3d")) {
            return context.contentResolver.openInputStream(url)
        } else {
            return URL(url.toString()).openStream()
        }
    }
}
