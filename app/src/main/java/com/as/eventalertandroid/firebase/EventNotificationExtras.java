package com.as.eventalertandroid.firebase;

public enum EventNotificationExtras {

    EVENT_ID("eventId"),
    CREATED_AT("createdAt"),
    TYPE_LABEL("typeLabel"),
    TYPE_IMAGE_PATH("typeImagePath"),
    SEVERITY_LABEL("severityLabel"),
    SEVERITY_COLOR("severityColor"),
    STATUS_LABEL("statusLabel"),
    STATUS_COLOR("statusColor"),
    IMPACT_RADIUS("impactRadius"),
    LATITUDE("latitude"),
    LONGITUDE("longitude");

    private final String key;

    EventNotificationExtras(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
