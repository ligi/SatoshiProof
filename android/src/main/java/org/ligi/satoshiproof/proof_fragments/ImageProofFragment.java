package org.ligi.satoshiproof.proof_fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import org.ligi.satoshiproof.FileProofController;
import org.ligi.satoshiproof.ImageFromIntentUriExtractor;
import org.ligi.satoshiproof.R;

public class ImageProofFragment extends Fragment implements ProofFragment {
    private static final int ACTIVITY_SELECT_IMAGE = 1;

    @Bind(R.id.imageView)
    ImageView imageView;
    private File selectedBitmapFile;

    @OnClick(R.id.selectImageButton)
    void pickPictureButtonOnClick() {
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void proof() {
        new FileProofController(getActivity()).proofFile(selectedBitmapFile);
    }

    @Override
    public String getTitle() {
        return "image";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    //showProgressDialog();
                    final ImageFromIntentUriExtractor imageExtractor = new ImageFromIntentUriExtractor(getActivity());
                    new Thread(new Runnable() {

                        @Override
                        public void run() {


                            selectedBitmapFile = imageExtractor.extract(imageReturnedIntent.getData());
                            //      proofFile(bitmapFile);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Bitmap bm = BitmapFactory.decodeFile(selectedBitmapFile.getAbsolutePath());
                                    imageView.setImageBitmap(bm);
                                }
                            });

                        }
                    }).start();
                }
        }
    }

}
