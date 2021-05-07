package com.as.eventalertandroid.ui.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.SyncHandler;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.auth0.android.jwt.JWT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        init();
    }

    private void init() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        if (!pref.contains(Constants.REFRESH_TOKEN) || !pref.contains(Constants.ACCESS_TOKEN)) {
            openAuthActivity();
            return;
        }

        String accessToken = pref.getString(Constants.ACCESS_TOKEN, null);
        String refreshToken = pref.getString(Constants.REFRESH_TOKEN, null);
        if (accessToken == null || refreshToken == null) {
            openAuthActivity();
            return;
        }

        JWT jwt = new JWT(refreshToken);
        if (jwt.isExpired(0)) {
            openAuthActivity();
            return;
        }

        Session session = Session.getInstance();
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);

        SyncHandler.runStartupSync()
                .thenAccept(aVoid -> runOnUiThread(this::openMainActivity))
                .exceptionally(throwable -> {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();
                    runOnUiThread(this::openAuthActivity);
                    return null;
                });
    }

    private void openAuthActivity() {
        Intent intent = new Intent(InitialActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent(InitialActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
