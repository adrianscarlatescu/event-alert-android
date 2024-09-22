package com.as.eventalertandroid.ui.main;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.enums.AppTab;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.ui.main.admin.AdminTabFragment;
import com.as.eventalertandroid.ui.main.creator.CreatorTabFragment;
import com.as.eventalertandroid.ui.main.home.HomeTabFragment;
import com.as.eventalertandroid.ui.main.notifications.NotificationsFragment;
import com.as.eventalertandroid.ui.main.notifications.NotificationsTabFragment;
import com.as.eventalertandroid.ui.main.profile.ProfileTabFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NotificationsFragment.CounterListener {

    @BindView(R.id.mainBottomNavigationView)
    BottomNavigationView bottomNavigationView;

    TextView notificationsCounterTextView;

    private boolean isTwiceClicked;
    private AppTab appTab = AppTab.HOME;
    private Session session = Session.getInstance();

    private Map<AppTab, TabFragment> fragments = new ArrayMap<>(5);

    {
        fragments.put(AppTab.HOME, new HomeTabFragment());
        fragments.put(AppTab.CREATOR, new CreatorTabFragment());
        fragments.put(AppTab.NOTIFICATIONS, new NotificationsTabFragment());
        fragments.put(AppTab.PROFILE, new ProfileTabFragment());
        fragments.put(AppTab.ADMIN, new AdminTabFragment());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initNotificationsTab();
        initNotificationsBadge();

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.mainMenuHome:
                    if (appTab != AppTab.HOME) {
                        openTab(AppTab.HOME);
                        appTab = AppTab.HOME;
                    }
                    break;
                case R.id.mainMenuCreator:
                    if (appTab != AppTab.CREATOR) {
                        openTab(AppTab.CREATOR);
                        appTab = AppTab.CREATOR;
                    }
                    break;
                case R.id.mainMenuNotifications:
                    if (appTab != AppTab.NOTIFICATIONS) {
                        openTab(AppTab.NOTIFICATIONS);
                        appTab = AppTab.NOTIFICATIONS;
                    }
                    break;
                case R.id.mainMenuProfile:
                    if (appTab != AppTab.PROFILE) {
                        openTab(AppTab.PROFILE);
                        appTab = AppTab.PROFILE;
                    }
                    break;
                case R.id.mainMenuAdmin:
                    if (appTab != AppTab.ADMIN) {
                        openTab(AppTab.ADMIN);
                        appTab = AppTab.ADMIN;
                    }
                    break;
            }
            return true;
        });

        if (!session.isAdminUser()) {
            bottomNavigationView.getMenu().removeItem(R.id.mainMenuAdmin);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Stream.of(AppTab.values()).forEach(o -> {
            Fragment fragment = fragments.get(o);
            String tag = String.valueOf(o.getIndex());
            if (fragment != null) {
                ft.add(R.id.mainContent, fragment, tag);
                ft.hide(fragment);
            }
        });

        Fragment activeFragment = fragments.get(appTab);
        if (activeFragment != null) {
            ft.show(activeFragment);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(i);
            if (fragment.isVisible() && fragment.getChildFragmentManager().getBackStackEntryCount() > 1) {
                ((TabFragment) fragment).onBackClicked();
                return;
            }
        }
        if (isTwiceClicked) {
            finishAffinity();
        }
        Toast.makeText(this, getString(R.string.message_back_twice), Toast.LENGTH_SHORT).show();
        session.getHandler().postDelayed(() -> isTwiceClicked = false, 3000);
        isTwiceClicked = true;
    }

    @Override
    public void onNotificationsCounterChange(NotificationsFragment fragment, long value) {
        notificationsCounterTextView.animate().alpha(0).setDuration(250).setStartDelay(250).withEndAction(() -> {
            if (value == 0) {
                notificationsCounterTextView.setVisibility(View.GONE);
                return;
            }

            if (notificationsCounterTextView.getVisibility() != View.VISIBLE) {
                notificationsCounterTextView.setVisibility(View.VISIBLE);
            }

            notificationsCounterTextView.animate().alpha(1).setDuration(250).start();

            if (value >= 100) {
                String limit = "99+";
                notificationsCounterTextView.setText(limit);
                notificationsCounterTextView.setTextSize(12f);
                return;
            }

            notificationsCounterTextView.setText(String.valueOf(value));
            if (value >= 10) {
                notificationsCounterTextView.setTextSize(14f);
                return;
            }
            notificationsCounterTextView.setTextSize(16f);
        });
    }

    public void setFragment(Fragment fragment) {
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment tabFragment = getSupportFragmentManager().getFragments().get(i);
            if (tabFragment.isVisible()) {
                ((TabFragment) tabFragment).setFragment(fragment);
                return;
            }
        }
    }

    private void openTab(AppTab appTab) {
        TabFragment fragmentToHide = fragments.get(this.appTab);
        TabFragment fragmentToShow = fragments.get(appTab);

        Objects.requireNonNull(fragmentToHide);
        Objects.requireNonNull(fragmentToShow);

        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentToHide)
                .show(fragmentToShow)
                .commit();

        fragmentToShow.onTabClicked();
    }

    private void initNotificationsBadge() {
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView bottomNavigationItemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(AppTab.NOTIFICATIONS.getIndex());
        View badgeView = LayoutInflater.from(this).inflate(R.layout.layout_notification, bottomNavigationItemView, true);

        notificationsCounterTextView = badgeView.findViewById(R.id.notificationsCounterTextView);
        notificationsCounterTextView.setVisibility(View.GONE);
    }

    private void initNotificationsTab() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null &&
                bundle.containsKey(Constants.EVENT_NOTIFICATION_HANDLE) &&
                bundle.getBoolean(Constants.EVENT_NOTIFICATION_HANDLE)) {

            appTab = AppTab.NOTIFICATIONS;
            bottomNavigationView.setSelectedItemId(R.id.mainMenuNotifications);

        }
    }

}
