package org.ligi.satoshiproof;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.params.MainNetParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import de.schildbach.wallet.integration.android.BitcoinIntegration;
import java.text.DateFormat;
import java.util.Date;

public class ProofAsyncTask extends AsyncTask<Void, String, String> {

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
        progressDialog.setMessage("Checking existence in blockchain");
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(@NonNull Void... voids) {
        address = new Address(MainNetParams.get(), Utils.sha256hash160(data));
        publishProgress("searching for Address: " + address.toString());

        try {

            final OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder().url("https://blockchain.info/de/q/addressfirstseen/" + address.toString()).build();

            final Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(@Nullable final String firstSeenDateString) {

        if (activity.isFinishing()) {
            return;
        }

        progressDialog.dismiss();

        final Long firstSeenLong = safelyToLong(firstSeenDateString);

        if (firstSeenLong == null) {
            String message = "there where network problems - please try again later";
            if (firstSeenDateString != null) {
                message += firstSeenDateString;
            }

            new AlertDialog.Builder(activity).setMessage(message).setPositiveButton(android.R.string.ok, null).show();
            return;
        }

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setPositiveButton(android.R.string.ok, null);
        if (firstSeenLong == 0) {
            alertBuilder.setMessage("The existence of this is not proven yet.");
            alertBuilder.setNeutralButton("Add Proof", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BitcoinIntegration.request(activity, address.toString(), 5460);
                }
            });

        } else {
            final String dateString = DateFormat.getDateTimeInstance().format(new Date(firstSeenLong * 1000));
            alertBuilder.setMessage("The existence of this was proven on:" + dateString);
        }
        alertBuilder.show();
        super.onPostExecute(firstSeenDateString);
    }

    private static Long safelyToLong(final @Nullable String firstSeenDateString) {
        try {
            return Long.parseLong(firstSeenDateString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
