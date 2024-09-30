package com.as.eventalertandroid.firebase;

public enum EventNotificationExtras {

    EVENT_ID("eventId"),
    EVENT_DATE_TIME("eventDateTime"),
    EVENT_TAG_NAME("eventTagName"),
    EVENT_TAG_IMAGE_PATH("eventTagImagePath"),
    EVENT_SEVERITY_NAME("eventSeverityName"),
    EVENT_SEVERITY_COLOR("eventSeverityColor"),
    EVENT_LATITUDE("eventLatitude"),
    EVENT_LONGITUDE("eventLongitude");

    private final String key;

    EventNotificationExtras(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
