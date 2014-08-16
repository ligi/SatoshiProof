package org.ligi.satoshiproof;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.ligi.axt.AXT;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.File;

public class MainActivity extends Activity {

    private static final int ACTIVITY_SELECT_IMAGE = 1;
    private TextView textView;
    private Handler handler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        setContentView(R.layout.activity_main);

        TraceDroid.init(this);
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);

        textView = (TextView) findViewById(R.id.textToProof);
        setupButtons();
        checkForMaterialToProveFromIntent();
    }

    private void checkForMaterialToProveFromIntent() {

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if ("text/plain".equals(getIntent().getType())) {
                handleSendText(getIntent()); // Handle text being sent
            } else {
                handleSendStream(getIntent()); // Handle single image being sent
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            showProgressDialog();
            new ProofAsyncTask(MainActivity.this, sharedText.getBytes()).execute();
        }
    }

    void handleSendStream(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            showProgressDialog();
            final ImageFromIntentUriExtractor imageExtractor = new ImageFromIntentUriExtractor(MainActivity.this);
            File bitmapFile = imageExtractor.extract(imageUri);
            proofFile(bitmapFile);

        }
    }


    private void setupButtons() {
        findViewById(R.id.proofTextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProofAsyncTask(MainActivity.this, textView.getText().toString().getBytes()).execute();
            }
        });

        findViewById(R.id.pickPictureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    final Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    showProgressDialog();
                    final ImageFromIntentUriExtractor imageExtractor = new ImageFromIntentUriExtractor(MainActivity.this);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            File bitmapFile = imageExtractor.extract(imageReturnedIntent.getData());
                            proofFile(bitmapFile);

                        }
                    }).start();
                }
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching file");
        progressDialog.show();
    }

    private void proofFile(final File file) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (file == null) {
                        failWitAlertDialog("Could not open file");
                        return;
                    }
                    final byte[] imageBytes = FileUtils.readFileToByteArray(file);
                    new ProofAsyncTask(MainActivity.this, imageBytes).execute();
                    progressDialog.dismiss();
                } catch (Exception e) {
                    failWitAlertDialog("Could not open " + file + " " + e);
                }
            }
        });

    }

    private void failWitAlertDialog(String msg) {
        progressDialog.dismiss();
        new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogDiscardingOnClickListener()).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AXT.at(this).startCommonIntent().activityFromClass(HelpDialogActivity.class);
                return true;
            case R.id.action_hash:
                AXT.at(this).startCommonIntent().activityFromClass(LastHashActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
