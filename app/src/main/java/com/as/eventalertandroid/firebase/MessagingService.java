package com.as.eventalertandroid.firebase;

import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.dao.EventNotificationDao;
import com.as.eventalertandroid.data.dao.SubscriptionDao;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.data.model.SubscriptionEntity;
import com.as.eventalertandroid.net.Session;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;

public class MessagingService extends FirebaseMessagingService {

    private Session session = Session.getInstance();
    private EventNotificationDao eventNotificationDao = LocalDatabase.getInstance().eventNotificationDao();
    private SubscriptionDao subscriptionDao = LocalDatabase.getInstance().subscriptionDao();

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

        CompletableFuture
                .runAsync(() -> {
                    List<SubscriptionEntity> subscriptionEntities = subscriptionDao.findAll();
                    List<EventNotificationEntity> eventNotificationEntities = subscriptionEntities.stream()
                            .map(subscriptionEntity -> {
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
                                eventNotificationEntity.setUserId(subscriptionEntity.getUserId());
                                return eventNotificationEntity;
                            })
                            .collect(Collectors.toList());

                    if (!eventNotificationEntities.isEmpty()) {
                        eventNotificationDao.insert(eventNotificationEntities);
                        if (session.getUser() != null) {
                            eventNotificationEntities.stream()
                                    .filter(entity -> entity.getUserId().longValue() == session.getUserId().longValue())
                                    .findFirst()
                                    .ifPresent(entity -> EventBus.getDefault().post(entity));
                        }
                    }
                });
    }

}
