package com.as.eventalertandroid.firebase;

import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.net.Session;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> messageMap = remoteMessage.getData();

        Long eventId = Long.valueOf(Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_ID_KEY)));
        String eventDateTime = Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_DATE_TIME_KEY));
        String eventTagName = Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_TAG_NAME_KEY));
        String eventTagImagePath = Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_TAG_IMAGE_PATH_KEY));
        String eventSeverityName = Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_SEVERITY_NAME_KEY));
        Integer eventSeverityColor = Integer.valueOf(Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_SEVERITY_COLOR_KEY)));
        Double eventLatitude = Double.valueOf(Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_LATITUDE_KEY)));
        Double eventLongitude = Double.valueOf(Objects.requireNonNull(messageMap.get(EventNotificationExtras.EVENT_LONGITUDE_KEY)));

        EventNotificationEntity notification = new EventNotificationEntity();
        notification.setEventId(eventId);
        notification.setEventDateTime(eventDateTime);
        notification.setEventTagName(eventTagName);
        notification.setEventTagImagePath(eventTagImagePath);
        notification.setEventSeverityName(eventSeverityName);
        notification.setEventSeverityColor(eventSeverityColor);
        notification.setEventLatitude(eventLatitude);
        notification.setEventLongitude(eventLongitude);
        notification.setViewed(false);
        notification.setUserId(Session.getInstance().getUser().id);

        CompletableFuture
                .supplyAsync(() -> {
                    LocalDatabase localDatabase = LocalDatabase.getInstance(getApplicationContext());
                    return localDatabase.eventNotificationDao().insert(notification);
                })
                .thenAccept(id -> {
                    notification.setId(id);
                    EventBus.getDefault().post(notification);
                });
    }

}
