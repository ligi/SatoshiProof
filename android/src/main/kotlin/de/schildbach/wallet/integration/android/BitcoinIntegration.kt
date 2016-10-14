/**
 * Copyright 2012-2013 the original author or authors.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schildbach.wallet.integration.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

/**
 * @author Andreas Schildbach
 */
object BitcoinIntegration {
    private val INTENT_EXTRA_TRANSACTION_HASH = "transaction_hash"

    /**
     * Request any amount of Bitcoins (probably a donation) from user, without feedback from the app.

     * @param context
     * *            Android context
     * *
     * @param address
     * *            Bitcoin address
     */
    fun request(context: Context, address: String) {
        val intent = makeIntent(address, null)

        start(context, intent)
    }

    /**
     * Request specific amount of Bitcoins from user, without feedback from the app.

     * @param context
     * *            Android context
     * *
     * @param address
     * *            Bitcoin address
     * *
     * @param amount
     * *            Bitcoin amount in nanocoins
     */
    fun request(context: Context, address: String, amount: Long) {
        val intent = makeIntent(address, amount)

        start(context, intent)
    }

    /**
     * Request any amount of Bitcoins (probably a donation) from user, with feedback from the app. Result intent can be
     * received by overriding [android.app.Activity.onActivityResult]. Result indicates either
     * [Activity.RESULT_OK] or [Activity.RESULT_CANCELED]. In the success case, use
     * [.transactionHashFromResult] to read the transaction hash from the intent.

     * Warning: A success indication is no guarantee! To be on the safe side, you must drive your own Bitcoin
     * infrastructure and validate the transaction.

     * @param context
     * *            Android context
     * *
     * @param address
     * *            Bitcoin address
     */
    fun requestForResult(activity: Activity, requestCode: Int, address: String) {
        val intent = makeIntent(address, null)

        startForResult(activity, requestCode, intent)
    }

    /**
     * Request specific amount of Bitcoins from user, with feedback from the app. Result intent can be received by
     * overriding [android.app.Activity.onActivityResult]. Result indicates either [Activity.RESULT_OK] or
     * [Activity.RESULT_CANCELED]. In the success case, use [.transactionHashFromResult] to read the
     * transaction hash from the intent.

     * Warning: A success indication is no guarantee! To be on the safe side, you must drive your own Bitcoin
     * infrastructure and validate the transaction.

     * @param context
     * *            Android context
     * *
     * @param address
     * *            Bitcoin address
     */
    fun requestForResult(activity: Activity, requestCode: Int, address: String, amount: Long) {
        val intent = makeIntent(address, amount)

        startForResult(activity, requestCode, intent)
    }

    /**
     * Put transaction hash into result intent. Meant for usage by Bitcoin wallet applications.

     * @param result
     * *            result intent
     * *
     * @param txHash
     * *            transaction hash
     */
    fun transactionHashToResult(result: Intent, txHash: String) {
        result.putExtra(INTENT_EXTRA_TRANSACTION_HASH, txHash)
        result.putExtra(INTENT_EXTRA_TRANSACTION_HASH_OLD, txHash)
    }

    /**
     * Get transaction hash from result intent. Meant for usage by applications initiating a Bitcoin payment.

     * You can use this hash to request the transaction from the Bitcoin network, in order to validate. For this, you
     * need your own Bitcoin infrastructure though. There is no guarantee that the transaction has ever been broadcasted
     * to the Bitcoin network.

     * @param result
     * *            result intent
     * *
     * @return transaction hash
     */
    fun transactionHashFromResult(result: Intent): String {
        val txHash = result.getStringExtra(INTENT_EXTRA_TRANSACTION_HASH)

        return txHash
    }

    private val NANOCOINS_PER_COIN = 100000000

    private fun makeIntent(address: String?, amount: Long?): Intent {
        val uri = StringBuilder("bitcoin:")
        if (address != null)
            uri.append(address)
        if (amount != null)
            uri.append("?amount=").append(String.format("%d.%08d", amount / NANOCOINS_PER_COIN, amount % NANOCOINS_PER_COIN))

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()))

        return intent
    }

    private fun start(context: Context, intent: Intent) {
        val pm = context.packageManager
        if (pm.resolveActivity(intent, 0) != null)
            context.startActivity(intent)
        else
            redirectToDownload(context)
    }

    private fun startForResult(activity: Activity, requestCode: Int, intent: Intent) {
        val pm = activity.packageManager
        if (pm.resolveActivity(intent, 0) != null)
            activity.startActivityForResult(intent, requestCode)
        else
            redirectToDownload(activity)
    }

    private fun redirectToDownload(context: Context) {
        Toast.makeText(context, "No Bitcoin application found.\nPlease install Bitcoin Wallet.", Toast.LENGTH_LONG).show()

        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=de.schildbach.wallet"))
        val binaryIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/bitcoin-wallet/downloads/list"))

        val pm = context.packageManager
        if (pm.resolveActivity(marketIntent, 0) != null)
            context.startActivity(marketIntent)
        else if (pm.resolveActivity(binaryIntent, 0) != null)
            context.startActivity(binaryIntent)
        // else out of luck
    }

    private val INTENT_EXTRA_TRANSACTION_HASH_OLD = "transaction_id"
}
