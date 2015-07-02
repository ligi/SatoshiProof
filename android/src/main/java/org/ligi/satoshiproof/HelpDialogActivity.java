package org.ligi.satoshiproof;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import org.ligi.axt.AXT;

public class HelpDialogActivity extends AppCompatActivity {

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

