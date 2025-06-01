package com.as.eventalertandroid.ui.main.reporter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.ui.main.TabFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ReporterTabFragment extends TabFragment {

    private ReporterFragment reporterFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reporterFragment = new ReporterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(reporterFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(reporterFragment::syncUserEvents, 200);
    }

}