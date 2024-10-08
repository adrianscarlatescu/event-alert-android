package com.as.eventalertandroid.ui.main.creator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.ui.main.TabFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CreatorTabFragment extends TabFragment {

    private CreatorFragment creatorFragment;
    private final Session session = Session.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creatorFragment = new CreatorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(creatorFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(creatorFragment::syncUserEvents, 200);
    }

}