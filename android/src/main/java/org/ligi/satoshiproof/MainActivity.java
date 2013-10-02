package org.ligi.satoshiproof;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.provider.MediaStore;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.apache.commons.io.FileUtils;
import org.ligi.androidhelper.helpers.dialog.DialogDiscardingOnClickListener;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.*;

/**
 * Created by ligi on 5/26/13.
 */
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
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de",this);

        textView = (TextView) findViewById(R.id.textToProof);
        setupButtons();
        checkForMaterialToProveFromIntent();
    }

    private void checkForMaterialToProveFromIntent() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else {
                handleSendStream(intent); // Handle single image being sent
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
            proofFile(bitmapFile );

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
                Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                            proofFile(bitmapFile );

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
                    if (file==null) {
                        failWitAlertDialog("Could not open file");
                        return;
                    }
                    byte[] imageBytes = FileUtils.readFileToByteArray(file);
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
                .setPositiveButton("OK", new DialogDiscardingOnClickListener()).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent helpIntent = new Intent(this, HelpDialogActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.action_hash:
                Intent hashIntent = new Intent(this, LastHashActivity.class);
                startActivity(hashIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
