package org.aerogear.mobile.example.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected BaseActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                    @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutResId(), container, false);

        ButterKnife.bind(this, view);

        activity = (BaseActivity) getActivity();

        return view;
    }

    abstract @LayoutRes int getLayoutResId();

}
