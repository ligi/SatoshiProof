package org.ligi.satoshiproof

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import java.io.IOException

object HelpDialog {

    fun show(context: Context) {
        val helpTextView = LayoutInflater.from(context).inflate(R.layout.help_dialog,null) as TextView

        helpTextView.movementMethod = LinkMovementMethod.getInstance()

        try {
            val helpInputStream = context.resources.openRawResource(R.raw.help)
            val helpString = helpInputStream.reader().readText()
            helpTextView.text = Html.fromHtml(helpString)
            AlertDialog.Builder(context)
                    .setTitle(R.string.help_title)
                    .setView(helpTextView)
                    .setPositiveButton(android.R.string.ok,null)
                    .show()
        } catch (ignored: IOException) {
            // should not happen
        }
    }
}

