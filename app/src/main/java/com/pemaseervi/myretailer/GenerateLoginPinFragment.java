package com.pemaseervi.myretailer;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GenerateLoginPinFragment extends Fragment {

    private GenerateLoginPinViewModel mViewModel;

    public static GenerateLoginPinFragment newInstance() {
        return new GenerateLoginPinFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generate_login_pin_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GenerateLoginPinViewModel.class);
        // TODO: Use the ViewModel
    }

}
