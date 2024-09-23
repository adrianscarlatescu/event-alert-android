package com.as.eventalertandroid.ui.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.firebase.EventNotificationExtras;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.SyncHandler;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.auth0.android.jwt.JWT;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private Session session = Session.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        init();
    }

    private void init() {
        LocalDatabase.init(getApplicationContext());

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
        if (jwt.isExpired(1)) {
            openAuthActivity();
            return;
        }

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
        intent.putExtra(Constants.EVENT_NOTIFICATION_HANDLE, isEventNotificationExtras());
        startActivity(intent);
        finish();
    }

    private boolean isEventNotificationExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || !bundle.containsKey(EventNotificationExtras.EVENT_ID_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_DATE_TIME_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_TAG_NAME_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_TAG_IMAGE_PATH_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_SEVERITY_NAME_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_SEVERITY_COLOR_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_LATITUDE_KEY)
                || !bundle.containsKey(EventNotificationExtras.EVENT_LONGITUDE_KEY)) {
            return false;
        }

        Long eventId = Long.valueOf(Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_ID_KEY)));
        String eventDateTime = Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_DATE_TIME_KEY));
        String eventTagName = Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_TAG_NAME_KEY));
        String eventTagImagePath = Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_TAG_IMAGE_PATH_KEY));
        String eventSeverityName = Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_SEVERITY_NAME_KEY));
        Integer eventSeverityColor = Integer.valueOf(Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_SEVERITY_COLOR_KEY)));
        Double eventLatitude = Double.valueOf(Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_LATITUDE_KEY)));
        Double eventLongitude = Double.valueOf(Objects.requireNonNull(bundle.getString(EventNotificationExtras.EVENT_LONGITUDE_KEY)));

        EventNotificationEntity eventNotificationEntity = new EventNotificationEntity();
        eventNotificationEntity.setEventId(eventId);
        eventNotificationEntity.setEventDateTime(eventDateTime);
        eventNotificationEntity.setEventTagName(eventTagName);
        eventNotificationEntity.setEventTagImagePath(eventTagImagePath);
        eventNotificationEntity.setEventSeverityName(eventSeverityName);
        eventNotificationEntity.setEventSeverityColor(eventSeverityColor);
        eventNotificationEntity.setEventLatitude(eventLatitude);
        eventNotificationEntity.setEventLongitude(eventLongitude);
        eventNotificationEntity.setViewed(false);
        eventNotificationEntity.setUserId(session.getUserId());

        CompletableFuture.runAsync(() -> LocalDatabase.getInstance().eventNotificationDao().insert(eventNotificationEntity));

        return true;
    }

}
