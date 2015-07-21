package com.eyeem.decorator.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyeem.decorator.annotation.Decorate;

/**
 * Created by budius on 21.07.15.
 */
public class DecoratedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag, container, false);
    }

    @Override
    @Decorate
    public void onStart() {
        super.onStart();
    }

    @Override
    @Decorate
    public void onStop() {
        super.onStop();
    }


}
