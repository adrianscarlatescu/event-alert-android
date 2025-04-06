package com.as.eventalertandroid.firebase;

import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.dao.EventNotificationDao;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.DeviceHandler;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.SubscriptionTokenUpdateDTO;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import androidx.annotation.NonNull;

public class MessagingService extends FirebaseMessagingService {

    private final SubscriptionService subscriptionService = RetrofitClient.getInstance().create(SubscriptionService.class);
    private final EventNotificationDao eventNotificationDao = LocalDatabase.getInstance().eventNotificationDao();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String currentLoggedInUserId = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).getString(Constants.USER_ID, null);
        if (currentLoggedInUserId == null) {
            return;
        }

        Map<String, String> messageMap = remoteMessage.getData();
        if (!Stream.of(EventNotificationExtras.values()).allMatch(eventExtras -> messageMap.containsKey(eventExtras.getKey()))) {
            return;
        }

        Long eventId = Long.valueOf(messageMap.get(EventNotificationExtras.EVENT_ID.getKey()));
        String createdAt = messageMap.get(EventNotificationExtras.CREATED_AT.getKey());
        String typeLabel = messageMap.get(EventNotificationExtras.TYPE_LABEL.getKey());
        String typeImagePath = messageMap.get(EventNotificationExtras.TYPE_IMAGE_PATH.getKey());
        String severityLabel = messageMap.get(EventNotificationExtras.SEVERITY_LABEL.getKey());
        String severityColor = messageMap.get(EventNotificationExtras.SEVERITY_COLOR.getKey());
        String statusLabel = messageMap.get(EventNotificationExtras.STATUS_LABEL.getKey());
        String statusColor = messageMap.get(EventNotificationExtras.STATUS_COLOR.getKey());
        String impactRadius = messageMap.get(EventNotificationExtras.IMPACT_RADIUS.getKey());
        Double latitude = Double.valueOf(messageMap.get(EventNotificationExtras.LATITUDE.getKey()));
        Double longitude = Double.valueOf(messageMap.get(EventNotificationExtras.LONGITUDE.getKey()));

        EventNotificationEntity eventNotificationEntity = new EventNotificationEntity();
        eventNotificationEntity.setEventId(eventId);
        eventNotificationEntity.setCreatedAt(createdAt);
        eventNotificationEntity.setTypeLabel(typeLabel);
        eventNotificationEntity.setTypeImagePath(typeImagePath);
        eventNotificationEntity.setSeverityLabel(severityLabel);
        eventNotificationEntity.setSeverityColor(severityColor);
        eventNotificationEntity.setStatusLabel(statusLabel);
        eventNotificationEntity.setStatusColor(statusColor);
        eventNotificationEntity.setImpactRadius(impactRadius);
        eventNotificationEntity.setLatitude(latitude);
        eventNotificationEntity.setLongitude(longitude);
        eventNotificationEntity.setViewed(false);
        eventNotificationEntity.setUserId(Long.valueOf(currentLoggedInUserId));

        CompletableFuture
                .supplyAsync(() -> eventNotificationDao.insert(eventNotificationEntity))
                .thenAccept(id -> {
                    eventNotificationEntity.setId(id);
                    EventBus.getDefault().post(eventNotificationEntity);
                });
    }

    /**
     * The token may change when:
     * - The app is restored on a new device
     * - The user uninstalls/reinstall the app
     * - The user clears app data.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        CompletableFuture
                .runAsync(() -> {
                    SubscriptionTokenUpdateDTO subscriptionTokenUpdate = new SubscriptionTokenUpdateDTO();
                    subscriptionTokenUpdate.firebaseToken = token;
                    subscriptionService.updateToken(DeviceHandler.getAndroidId(getApplicationContext()), subscriptionTokenUpdate);
                })
                .exceptionally(throwable -> null);
    }

}
