package com.as.eventalertandroid.firebase;

import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.dao.EventNotificationDao;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.request.SubscriptionTokenRequest;
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
        String eventDateTime = messageMap.get(EventNotificationExtras.EVENT_DATE_TIME.getKey());
        String eventTagName = messageMap.get(EventNotificationExtras.EVENT_TAG_NAME.getKey());
        String eventTagImagePath = messageMap.get(EventNotificationExtras.EVENT_TAG_IMAGE_PATH.getKey());
        String eventSeverityName = messageMap.get(EventNotificationExtras.EVENT_SEVERITY_NAME.getKey());
        Integer eventSeverityColor = Integer.valueOf(messageMap.get(EventNotificationExtras.EVENT_SEVERITY_COLOR.getKey()));
        Double eventLatitude = Double.valueOf(messageMap.get(EventNotificationExtras.EVENT_LATITUDE.getKey()));
        Double eventLongitude = Double.valueOf(messageMap.get(EventNotificationExtras.EVENT_LONGITUDE.getKey()));

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

        CompletableFuture
                .supplyAsync(() -> eventNotificationDao.insert(eventNotificationEntity))
                .thenAccept(id -> {
                    eventNotificationEntity.setId(id);
                    EventBus.getDefault().post(eventNotificationEntity);
                });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        /*CompletableFuture
                .supplyAsync(subscriptionDao::findAll)
                .thenCompose(subscriptionEntities -> {
                    if (subscriptionEntities.isEmpty()) {
                        return CompletableFuture.completedFuture(null);
                    }

                    SubscriptionTokenRequest subscriptionTokenRequest = new SubscriptionTokenRequest();
                    subscriptionTokenRequest.deviceId = "test";
                    subscriptionTokenRequest.firebaseToken = token;

                    return subscriptionService.updateTokens(subscriptionTokenRequest);
                })
                .thenAccept(aVoid -> subscriptionDao.updateFirebaseToken(token))
                .exceptionally(throwable -> {
                    subscriptionDao.deleteAll(); // All subscriptions become invalid
                    return null;
                });*/
    }

}
