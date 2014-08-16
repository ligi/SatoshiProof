package org.ligi.satoshiproof;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import org.ligi.axt.AXT;

import java.io.IOException;
import java.io.InputStream;

public class HelpDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Help");
        setContentView(R.layout.help_dialog);
        final TextView tv = (TextView) findViewById(R.id.helpTextView);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        try {
            final InputStream helpInputStream = getResources().openRawResource(R.raw.help);
            final String helpString = AXT.at(helpInputStream).readToString();
            tv.setText(Html.fromHtml(helpString));
        } catch (IOException e) {
            finish();
        }

    }

}

