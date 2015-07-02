package org.ligi.satoshiproof.proof_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.ligi.satoshiproof.ProofAsyncTask;
import org.ligi.satoshiproof.R;

public class TextProofFragment extends Fragment implements ProofFragment {

    @Bind(R.id.textToProof)
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_text, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void proof() {
        new ProofAsyncTask(getActivity(), textView.getText().toString().getBytes()).execute();
    }

    @Override
    public String getTitle() {
        return "text";
    }
}
