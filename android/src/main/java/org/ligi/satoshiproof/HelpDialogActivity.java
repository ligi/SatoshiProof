package org.ligi.satoshiproof;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ligi on 5/27/13.
 */
public class HelpDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Help");
        setContentView(R.layout.help_dialog);
        TextView tv = (TextView) findViewById(R.id.helpTextView);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(Html.fromHtml(getStringFromRaw("help.html")));
    }


    private String getStringFromRaw(String path) {

        String xmlString = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.help);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            xmlString = new String(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return xmlString;
    }
}

