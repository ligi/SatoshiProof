package org.ligi.satoshiproof.proof_fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_text.*
import org.ligi.satoshiproof.ProofAsyncTask
import org.ligi.satoshiproof.R

class TextProofFragment : Fragment(), ProofFragment {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_text, container, false)
    }

    override fun proof() {
        ProofAsyncTask(activity, textToProof.text.toString().toByteArray()).execute()
    }

    override val title = "text"
}
