package com.as.eventalertandroid.ui.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.ui.main.TabFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeTabFragment extends TabFragment {

    private HomeFragment homeFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeFragment = new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(homeFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {
        // Nothing to do
    }

}
