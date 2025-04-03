package com.as.eventalertandroid.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.DeviceHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.handler.SyncHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.AuthLoginDTO;
import com.as.eventalertandroid.net.model.AuthRegisterDTO;
import com.as.eventalertandroid.net.model.SubscriptionStatusUpdateDTO;
import com.as.eventalertandroid.net.service.AuthService;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    @BindView(R.id.authToolbar)
    Toolbar toolbar;
    @BindView(R.id.authTabLayout)
    TabLayout tabLayout;
    @BindView(R.id.authViewPager)
    ViewPager viewPager;

    private ViewPagerAdapter adapter;
    private final Session session = Session.getInstance();
    private final AuthService authService = RetrofitClient.getInstance().create(AuthService.class);
    private final SubscriptionService subscriptionService = RetrofitClient.getInstance().create(SubscriptionService.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new RegisterFragment(), "Register");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onLoginRequest(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(AuthActivity.this, R.string.message_email_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(AuthActivity.this, R.string.message_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(AuthActivity.this, R.string.message_password_required, Toast.LENGTH_SHORT).show();
            return;
        }

        AuthLoginDTO loginRequest = new AuthLoginDTO();
        loginRequest.email = email;
        loginRequest.password = password;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        authService.login(loginRequest)
                .thenCompose(authTokens -> {
                    session.setAccessToken(authTokens.accessToken);
                    session.setRefreshToken(authTokens.refreshToken);

                    getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                            .edit()
                            .putString(Constants.ACCESS_TOKEN, authTokens.accessToken)
                            .putString(Constants.REFRESH_TOKEN, authTokens.refreshToken)
                            .putString(Constants.USER_EMAIL, email)
                            .putString(Constants.USER_PASSWORD, password)
                            .apply();

                    return SyncHandler.runStartupSync(AuthActivity.this);
                })
                .thenCompose(aVoid -> {
                    getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                            .edit()
                            .putString(Constants.USER_ID, String.valueOf(session.getUserId()))
                            .apply();

                    if (session.getSubscription() == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    SubscriptionStatusUpdateDTO subscriptionStatusUpdate = new SubscriptionStatusUpdateDTO();
                    subscriptionStatusUpdate.isActive = true;
                    return subscriptionService.updateStatus(session.getUserId(), DeviceHandler.getAndroidId(AuthActivity.this), subscriptionStatusUpdate);
                })
                .thenAccept(subscription -> {
                    progressDialog.dismiss();

                    runOnUiThread(() -> {
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                })
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(this, throwable);
                    return null;
                });
    }

    @Override
    public void onRegisterRequest(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            Toast.makeText(AuthActivity.this, R.string.message_email_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.length() > Constants.MAX_EMAIL_LENGTH) {
            String message = String.format(getString(R.string.message_email_length), Constants.MAX_EMAIL_LENGTH);
            Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(AuthActivity.this, R.string.message_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(AuthActivity.this, R.string.message_password_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH || password.length() > Constants.MAX_PASSWORD_LENGTH) {
            String message = String.format(getString(R.string.message_password_length),
                    Constants.MIN_PASSWORD_LENGTH, Constants.MAX_PASSWORD_LENGTH);
            Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(AuthActivity.this, R.string.message_confirmation_password_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(AuthActivity.this, R.string.message_different_passwords, Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRegisterDTO registerRequest = new AuthRegisterDTO();
        registerRequest.email = email;
        registerRequest.password = password;
        registerRequest.confirmPassword = confirmPassword;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        authService.register(registerRequest)
                .thenAccept(user -> {
                    progressDialog.dismiss();
                    RegisterFragment registerFragment = (RegisterFragment) adapter.getItem(1);
                    LoginFragment loginFragment = (LoginFragment) adapter.getItem(0);

                    runOnUiThread(() -> {
                        registerFragment.clearFields();
                        loginFragment.setFields(email, password);
                        viewPager.setCurrentItem(0);
                        Toast.makeText(AuthActivity.this, R.string.message_success, Toast.LENGTH_SHORT).show();
                    });
                })
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(this, throwable);
                    return null;
                });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public @NonNull
        Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitles.get(position);
        }
    }

}
