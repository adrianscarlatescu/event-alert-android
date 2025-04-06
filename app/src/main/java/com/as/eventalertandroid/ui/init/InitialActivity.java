package com.as.eventalertandroid.ui.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.dao.EventNotificationDao;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.firebase.EventNotificationExtras;
import com.as.eventalertandroid.handler.JwtHandler;
import com.as.eventalertandroid.handler.SyncHandler;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.main.MainActivity;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private final Session session = Session.getInstance();
    private final EventNotificationDao eventNotificationDao = LocalDatabase.getInstance().eventNotificationDao();

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

        if (JwtHandler.isExpired(refreshToken)) {
            Toast.makeText(InitialActivity.this, R.string.message_authorization_expired_error, Toast.LENGTH_SHORT).show();
            openAuthActivity();
            return;
        }

        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);

        SyncHandler.runStartupSync(InitialActivity.this)
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
        return bundle != null &&
                Stream.of(EventNotificationExtras.values()).allMatch(eventExtras -> bundle.containsKey(eventExtras.getKey()));
    }

    private CompletableFuture<Void> saveEventFromNotification() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return CompletableFuture.completedFuture(null);
        }
        String currentLoggedInUserId = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).getString(Constants.USER_ID, null);
        if (currentLoggedInUserId == null) {
            return CompletableFuture.completedFuture(null);
        }

        Long eventId = Long.valueOf(bundle.getString(EventNotificationExtras.EVENT_ID.getKey()));
        String createdAt = bundle.getString(EventNotificationExtras.CREATED_AT.getKey());
        String typeLabel = bundle.getString(EventNotificationExtras.TYPE_LABEL.getKey());
        String typeImagePath = bundle.getString(EventNotificationExtras.TYPE_IMAGE_PATH.getKey());
        String severityLabel = bundle.getString(EventNotificationExtras.SEVERITY_LABEL.getKey());
        String severityColor = bundle.getString(EventNotificationExtras.SEVERITY_COLOR.getKey());
        String statusLabel = bundle.getString(EventNotificationExtras.STATUS_LABEL.getKey());
        String statusColor = bundle.getString(EventNotificationExtras.STATUS_COLOR.getKey());
        String impactRadius = bundle.getString(EventNotificationExtras.IMPACT_RADIUS.getKey());
        Double latitude = Double.valueOf(bundle.getString(EventNotificationExtras.LATITUDE.getKey()));
        Double longitude = Double.valueOf(bundle.getString(EventNotificationExtras.LONGITUDE.getKey()));

        EventNotificationEntity eventNotificationEntity = new EventNotificationEntity();
        eventNotificationEntity.setEventId(eventId);
        eventNotificationEntity.setCreatedAt(createdAt);
        eventNotificationEntity.setTypeLabel(typeLabel);
        eventNotificationEntity.setTypeImagePath(typeImagePath);
        eventNotificationEntity.setSeverityLabel(severityLabel);
        eventNotificationEntity.setSeverityColor(severityColor);
        eventNotificationEntity.setStatusLabel(statusLabel);
        eventNotificationEntity.setStatusColor(statusColor);
        eventNotificationEntity.setLatitude(latitude);
        eventNotificationEntity.setLongitude(longitude);
        eventNotificationEntity.setImpactRadius(impactRadius);
        eventNotificationEntity.setViewed(false);
        eventNotificationEntity.setUserId(Long.valueOf(currentLoggedInUserId));

        return CompletableFuture.runAsync(() -> eventNotificationDao.insert(eventNotificationEntity));
    }

}
