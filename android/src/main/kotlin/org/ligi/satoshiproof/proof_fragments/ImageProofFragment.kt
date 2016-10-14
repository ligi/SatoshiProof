package org.ligi.satoshiproof.proof_fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_image.*
import org.ligi.satoshiproof.FileProofController
import org.ligi.satoshiproof.ImageFromIntentUriExtractor
import org.ligi.satoshiproof.R
import java.io.File

class ImageProofFragment : Fragment(), ProofFragment {

    private var selectedBitmapFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onStart() {
        super.onStart()

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, ACTIVITY_SELECT_IMAGE)
        }
    }

    override fun proof() {
        FileProofController(activity).proofFile(selectedBitmapFile)
    }

    override val title = "image"

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        when (requestCode) {
            ACTIVITY_SELECT_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                //showProgressDialog();
                val imageExtractor = ImageFromIntentUriExtractor(activity)
                Thread(Runnable {
                    selectedBitmapFile = imageExtractor.extract(imageReturnedIntent!!.data)
                    //      proofFile(bitmapFile);

                    activity.runOnUiThread {
                        val bm = BitmapFactory.decodeFile(selectedBitmapFile!!.absolutePath)
                        imageView!!.setImageBitmap(bm)
                    }
                }).start()
            }
        }
    }

    companion object {
        private val ACTIVITY_SELECT_IMAGE = 1
    }

}
