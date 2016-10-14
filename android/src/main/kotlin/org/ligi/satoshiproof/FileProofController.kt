package org.ligi.satoshiproof

import android.app.Activity
import android.support.v7.app.AlertDialog
import org.apache.commons.io.FileUtils
import java.io.File

class FileProofController(private val context: Activity) {


    fun proofFile(file: File?) {
        context.runOnUiThread(Runnable {
            try {
                if (file == null) {
                    failWitAlertDialog("Could not open file")
                    return@Runnable
                }
                val imageBytes = FileUtils.readFileToByteArray(file)
                ProofAsyncTask(context, imageBytes).execute()
            } catch (e: Exception) {
                failWitAlertDialog("Could not open $file $e")
            }
        })
    }

    private fun failWitAlertDialog(msg: String) {
        AlertDialog.Builder(context).setMessage(msg).setPositiveButton(android.R.string.ok, null).show()
    }

}
