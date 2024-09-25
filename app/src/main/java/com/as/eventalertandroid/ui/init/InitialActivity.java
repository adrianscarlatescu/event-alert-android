package com.as.eventalertandroid.ui.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.dao.EventNotificationDao;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.firebase.EventNotificationExtras;
import com.as.eventalertandroid.net.JwtUtils;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.SyncHandler;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.main.MainActivity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private Session session = Session.getInstance();
    private EventNotificationDao eventNotificationDao = LocalDatabase.getInstance().eventNotificationDao();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        if (isEventNotificationExtras()) {
            saveEventFromNotification().thenAccept(aVoid -> init());
        } else {
            init();
        }
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

        if (JwtUtils.isExpired(refreshToken)) {
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
        return bundle != null && bundle.containsKey(EventNotificationExtras.EVENT_ID_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_DATE_TIME_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_TAG_NAME_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_TAG_IMAGE_PATH_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_SEVERITY_NAME_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_SEVERITY_COLOR_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_LATITUDE_KEY)
                && bundle.containsKey(EventNotificationExtras.EVENT_LONGITUDE_KEY);
    }

    private CompletableFuture<Void> saveEventFromNotification() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return CompletableFuture.completedFuture(null);
        }

        String currentLoggedInUserId = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).getString(Constants.USER_ID, null);

        if (currentLoggedInUserId != null) {
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
            eventNotificationEntity.setUserId(Long.valueOf(currentLoggedInUserId));

            return CompletableFuture.runAsync(() -> eventNotificationDao.insert(eventNotificationEntity));
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

}
