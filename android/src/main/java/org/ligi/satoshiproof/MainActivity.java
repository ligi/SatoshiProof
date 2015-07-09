package org.ligi.satoshiproof;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import org.ligi.axt.AXT;
import org.ligi.satoshiproof.proof_fragments.ImageProofFragment;
import org.ligi.satoshiproof.proof_fragments.ProofFragment;
import org.ligi.satoshiproof.proof_fragments.TextProofFragment;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @OnClick(R.id.proofFAB)
    void proofFABClick() {
        final ProofFragment page =(ProofFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
        page.proof();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        final FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(final int position) {
                switch (position) {
                    case 0:
                        return new TextProofFragment();
                    default:
                        return new ImageProofFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(final int position) {
                return ((ProofFragment) getItem(position)).getTitle();
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


        TraceDroid.init(this);
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);

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
        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            new ProofAsyncTask(MainActivity.this, sharedText.getBytes()).execute();
        }
    }

    void handleSendStream(Intent intent) {
        final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            final ImageFromIntentUriExtractor imageExtractor = new ImageFromIntentUriExtractor(MainActivity.this);
            final File bitmapFile = imageExtractor.extract(imageUri);
            new FileProofController(this).proofFile(bitmapFile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
