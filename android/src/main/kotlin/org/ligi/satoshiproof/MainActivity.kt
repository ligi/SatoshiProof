package org.ligi.satoshiproof

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.ligi.satoshiproof.proof_fragments.ImageProofFragment
import org.ligi.satoshiproof.proof_fragments.ProofFragment
import org.ligi.satoshiproof.proof_fragments.TextProofFragment
import org.ligi.tracedroid.TraceDroid
import org.ligi.tracedroid.sending.TraceDroidEmailSender

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        proofFAB.setOnClickListener {
            val page = viewPager.adapter.instantiateItem(viewPager, viewPager.currentItem) as ProofFragment
            page.proof()
        }

        val fragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> return TextProofFragment()
                    else -> return ImageProofFragment()
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence {
                return (getItem(position) as ProofFragment).title
            }
        }

        viewPager.adapter = fragmentPagerAdapter

        sliding_tabs.setupWithViewPager(viewPager)

        TraceDroid.init(this)
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)

        checkForMaterialToProveFromIntent()
    }

    private fun checkForMaterialToProveFromIntent() {

        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            if ("text/plain" == intent.type) {
                handleSendText(intent) // Handle text being sent
            } else {
                handleSendStream(intent) // Handle single image being sent
            }
        }
    }

    internal fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            ProofAsyncTask(this@MainActivity, sharedText.toByteArray()).execute()
        }
    }

    internal fun handleSendStream(intent: Intent) {
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            val imageExtractor = ImageFromIntentUriExtractor(this@MainActivity)
            val bitmapFile = imageExtractor.extract(imageUri)
            FileProofController(this).proofFile(bitmapFile)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                HelpDialog.show(this)
                return true
            }
            R.id.action_hash -> {
                startActivity(Intent(this,LastHashActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
