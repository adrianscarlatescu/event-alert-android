package com.as.eventalertandroid.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.SyncHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.body.AuthLoginBody;
import com.as.eventalertandroid.net.model.body.AuthRegisterBody;
import com.as.eventalertandroid.net.service.AuthService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private ViewPagerAdapter adapter;
    private AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);

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
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        Session session = Session.getInstance();

        AuthLoginBody body = new AuthLoginBody();
        body.email = email;
        body.password = password;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        authService.login(body)
                .thenCompose(authTokens -> {
                    session.setAuthTokens(authTokens);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Constants.ACCESS_TOKEN, authTokens.accessToken);
                    editor.putString(Constants.REFRESH_TOKEN, authTokens.refreshToken);
                    editor.putString(Constants.USER_EMAIL, email);
                    editor.putString(Constants.USER_PASSWORD, password);
                    editor.apply();

                    return SyncHandler.runStartupSync();
                })
                .thenAccept(aVoid -> {
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
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8 || password.length() > 40) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_password_length), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_password_not_identical), Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRegisterBody body = new AuthRegisterBody();
        body.email = email;
        body.password = password;
        body.confirmPassword = confirmPassword;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        authService.register(body)
                .thenAccept(user -> {
                    progressDialog.dismiss();
                    RegisterFragment registerFragment = (RegisterFragment) adapter.getItem(1);
                    LoginFragment loginFragment = (LoginFragment) adapter.getItem(0);

                    runOnUiThread(() -> {
                        registerFragment.clearFields();
                        loginFragment.setFields(email, password);
                        viewPager.setCurrentItem(0);
                        Toast.makeText(AuthActivity.this, getString(R.string.message_success), Toast.LENGTH_SHORT).show();
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
