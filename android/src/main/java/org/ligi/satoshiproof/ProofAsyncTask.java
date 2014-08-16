package org.ligi.satoshiproof;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.params.MainNetParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.ligi.axt.listeners.DialogDiscardingOnClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.schildbach.wallet.integration.android.BitcoinIntegration;

class ProofAsyncTask extends AsyncTask<Void, String, String> {

    private Address address;
    private final byte[] data;
    private final Activity activity;
    private final ProgressDialog progressDialog;

    public ProofAsyncTask(Activity activity, byte[] data) {
        this.data = data;
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Checking this text");
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Void... voids) {
        address = new Address(MainNetParams.get(), Utils.sha256hash160(data));
        publishProgress("searching for Address: " + address.toString());

        try {

            final OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url("http://blockexplorer.com/q/addressfirstseen/" + address.toString())
                    .build();

            final Response response = client.newCall(request).execute();
            return response.body().string();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String firstSeenDateString) {

        if (activity.isFinishing()) {
            return;
        }

        progressDialog.dismiss();

        if(firstSeenDateString==null) {
            new AlertDialog.Builder(activity)
                    .setMessage("there where network problems - please try again later")
                    .setPositiveButton(android.R.string.ok,null)
                    .show();
            return;
        }

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setPositiveButton("OK", new DialogDiscardingOnClickListener());
        if (firstSeenDateString.toLowerCase(Locale.getDefault()).startsWith("never seen")) {
            alertBuilder.setMessage("The existence of this is not proven yet.");
            alertBuilder.setNeutralButton("Add Proof", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BitcoinIntegration.request(activity, address.toString(), 1);
                }
            });

        } else {
            final String dateString = getString(firstSeenDateString);
            alertBuilder.setMessage("The existence of this was proven on:" + dateString);
        }
        alertBuilder.show();
        super.onPostExecute(firstSeenDateString);
    }

    private String getString(String firstSeenDateString) {

        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            final Date date = dateFormat.parse(firstSeenDateString);
            return date.toString();
        } catch (ParseException e) {
        }

        return firstSeenDateString + " UTC";
    }
}
