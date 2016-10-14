package org.ligi.satoshiproof

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.AsyncTask
import de.schildbach.wallet.integration.android.BitcoinIntegration
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ligi.satoshiproof.util.AddressGenerator.dataToAddressString
import java.text.DateFormat
import java.util.*

class ProofAsyncTask(private val activity: Activity, private val data: ByteArray) : AsyncTask<Void, String, String>() {

    private var addressString: String? = null
    private val progressDialog by lazy {  ProgressDialog(activity) }

    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog.setMessage("Checking existence in blockchain")
        progressDialog.show()
    }

    override fun onProgressUpdate(vararg values: String) {
        progressDialog.setMessage(values[0])
        super.onProgressUpdate(*values)
    }

    override fun doInBackground(vararg voids: Void): String? {
        addressString = dataToAddressString(data)
        publishProgress("searching for Address: " + addressString!!)

        try {

            val client = OkHttpClient()

            val request = Request.Builder().url("https://blockchain.info/de/q/addressfirstseen/" + addressString!!).build()

            val response = client.newCall(request).execute()
            return response.body().string()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(firstSeenDateString: String?) {

        if (activity.isFinishing) {
            return
        }

        progressDialog.dismiss()

        val firstSeenLong = safelyToLong(firstSeenDateString)

        if (firstSeenLong == null) {
            var message = "there where network problems - please try again later"
            if (firstSeenDateString != null) {
                message += firstSeenDateString
            }

            AlertDialog.Builder(activity).setMessage(message).setPositiveButton(android.R.string.ok, null).show()
            return
        }

        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setPositiveButton(android.R.string.ok, null)
        if (firstSeenLong == 0L) {
            alertBuilder.setMessage("The existence of this is not proven yet.")
            alertBuilder.setNeutralButton("Add Proof") { dialogInterface, i -> BitcoinIntegration.request(activity, addressString, 5460) }

        } else {
            val dateString = DateFormat.getDateTimeInstance().format(Date(firstSeenLong * 1000))
            alertBuilder.setMessage("The existence of this was proven on:" + dateString)
        }
        alertBuilder.show()
        super.onPostExecute(firstSeenDateString)
    }

    private fun safelyToLong(firstSeenDateString: String?): Long? {
        try {
            return java.lang.Long.parseLong(firstSeenDateString)
        } catch (e: NumberFormatException) {
            return null
        }
    }
}
