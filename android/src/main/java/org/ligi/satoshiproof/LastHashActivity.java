package org.ligi.satoshiproof;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.androidquery.AQuery;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;

import java.net.MalformedURLException;
import java.net.URL;

public class LastHashActivity extends Activity {

    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Last Hash");
        setContentView(R.layout.last_hash);
        aQuery = new AQuery(this);
        new FetchLastHashAsyncTask().execute();
        setDisplayHomeAsUpEnabledIfPossible();
    }

    @TargetApi(11)
    private void setDisplayHomeAsUpEnabledIfPossible() {
        if (Build.VERSION.SDK_INT>=11) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class FetchLastHashAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                final URL url = new URL("https://blockexplorer.com/q/latesthash");
                return AXT.at(url).downloadToString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s==null) {
                new AlertDialog.Builder(LastHashActivity.this)
                        .setMessage("Could not connect to network - please try again later.")
                        .setPositiveButton(android.R.string.ok,new ActivityFinishingOnClickListener(LastHashActivity.this))
                        .show();
                return;
            }
            final String url = "http://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=" + s.replace("\n", "");
            final AQuery hashImage = aQuery.id(R.id.hash_image);
            hashImage.visible();
            hashImage.image(url, true, false); // memcache yes - disk no as we want recent stuff
            aQuery.id(R.id.hash_text).text(s);
            super.onPostExecute(s);
        }
    }

}

