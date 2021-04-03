package com.pemaseervi.myretailer;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ShareFragment extends Fragment {

    private ShareViewModel mViewModel;
    private Button btn_share;
    public static ShareFragment newInstance() {
        return new ShareFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.share_fragment, container, false);
        Button btn_share=(Button)view.findViewById(R.id.share_btn);
        btn_share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareIt();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShareViewModel.class);
        // TODO: Use the ViewModel
    }
    private void shareIt() {

        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Namaste retailer application");
        intent.putExtra(Intent.EXTRA_TEXT,"Download namaste retailer by clicking on this link https://api.mywholesalerstore.com/apk/download ");
        intent.setType("text/plain");
        startActivity(intent);

    }
}
