package org.ligi.satoshiproof

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.androidquery.AQuery
import org.json.JSONException
import org.json.JSONObject
import org.ligi.axt.AXT
import org.ligi.axt.listeners.ActivityFinishingOnClickListener
import java.net.MalformedURLException
import java.net.URL

class LastHashActivity : AppCompatActivity() {

    private val aQuery by lazy { AQuery(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Last Hash"
        setContentView(R.layout.last_hash)
        FetchLastHashAsyncTask().execute()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class FetchLastHashAsyncTask : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg voids: Void): String? {
            try {
                val url = URL("https://api.biteasy.com/blockchain/v1/blocks?per_page=1")
                val s = AXT.at(url).downloadToString()
                if (s != null) {
                    val jsonObject = JSONObject(s)
                    return jsonObject.getJSONObject("data").getJSONArray("blocks").getJSONObject(0).getString("hash")
                }

            } catch (ignored: MalformedURLException) {
            } catch (ignored: JSONException) {
            }

            try {
                // fallback - was not working recently but might come back and then be a fallback
                val url = URL("https://blockexplorer.com/q/latesthash")
                return AXT.at(url).downloadToString()
            } catch (ignored: Exception) {

            }

            return null
        }

        override fun onPostExecute(s: String?) {
            if (s == null) {
                AlertDialog.Builder(this@LastHashActivity).setMessage("Could not connect to network - please try again later.").setPositiveButton(android.R.string.ok,
                        ActivityFinishingOnClickListener(this@LastHashActivity)).show()
                return
            }
            val url = "http://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=" + s.replace("\n", "")
            val hashImage = aQuery.id(R.id.hash_image)
            hashImage.visible()
            hashImage.image(url, true, false) // memcache yes - disk no as we want recent stuff
            aQuery.id(R.id.hash_text).text(s)
            super.onPostExecute(s)
        }
    }

}

