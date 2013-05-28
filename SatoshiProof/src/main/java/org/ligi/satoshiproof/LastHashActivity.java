package org.ligi.satoshiproof;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.androidquery.AQuery;
import org.ligi.androidhelper.AndroidHelper;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ligi on 5/28/13.
 */
public class LastHashActivity extends Activity {

    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Last Hash");
        setContentView(R.layout.last_hash);
        aQuery = new AQuery(this);
        new FetchLastHashAsyncTask().execute();
    }

    class FetchLastHashAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String res = null;
            try {
                URL url = new URL("https://blockexplorer.com/q/latesthash");
                res = AndroidHelper.at(url).downloadToString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            String url = "http://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=" + s.replace("\n", "");
            aQuery.id(R.id.hash_image).image(url, true, false);
            aQuery.id(R.id.hash_text).text(s);
            super.onPostExecute(s);
        }
    }

}

