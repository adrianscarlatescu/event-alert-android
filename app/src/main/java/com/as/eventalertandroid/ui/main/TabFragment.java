package com.as.eventalertandroid.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.R;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class TabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    public void setInitialFragment(Fragment fragment) {
        setFragment(fragment, false);
    }

    public void setFragment(Fragment fragment) {
        setFragment(fragment, true);
    }

    public void setFragment(Fragment fragment, boolean isWithAnimation) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (isWithAnimation) {
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }
        ft.replace(R.id.tabContent, fragment)
                .addToBackStack("TAG" + new Date())
                .commit();
    }

    public void onBackClicked() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStackImmediate();
        }
    }

    protected abstract void onTabClicked();

}
