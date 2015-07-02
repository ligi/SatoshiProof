package org.ligi.satoshiproof;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;

public class FileProofController {
    private final Activity context;

    public FileProofController(final Activity context) {
        this.context = context;
    }


    public void proofFile(final File file) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (file == null) {
                        failWitAlertDialog("Could not open file");
                        return;
                    }
                    final byte[] imageBytes = FileUtils.readFileToByteArray(file);
                    new ProofAsyncTask(context, imageBytes).execute();
                } catch (Exception e) {
                    failWitAlertDialog("Could not open " + file + " " + e);
                }
            }
        });
    }


    private void failWitAlertDialog(String msg) {
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(android.R.string.ok, new DialogDiscardingOnClickListener()).show();
    }

}
