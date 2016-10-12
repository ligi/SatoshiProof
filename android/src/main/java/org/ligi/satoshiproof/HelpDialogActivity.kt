package org.ligi.satoshiproof

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.help_dialog.*
import org.ligi.axt.AXT
import java.io.IOException

class HelpDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Help"
        setContentView(R.layout.help_dialog)
        helpTextView.movementMethod = LinkMovementMethod.getInstance()

        try {
            val helpInputStream = resources.openRawResource(R.raw.help)
            val helpString = AXT.at(helpInputStream).readToString()
            helpTextView.text = Html.fromHtml(helpString)
        } catch (e: IOException) {
            finish()
        }

    }

}

