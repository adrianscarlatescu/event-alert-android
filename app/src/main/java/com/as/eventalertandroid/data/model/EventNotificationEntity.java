package com.as.eventalertandroid.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EventNotificationEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long userId;
    private Boolean isViewed;

    private Long eventId;
    private String eventDateTime;
    private String eventTagName;
    private String eventTagImagePath;
    private String eventSeverityName;
    private Integer eventSeverityColor;
    private Double eventLatitude;
    private Double eventLongitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getViewed() {
        return isViewed;
    }

    public void setViewed(Boolean viewed) {
        isViewed = viewed;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getEventTagName() {
        return eventTagName;
    }

    public void setEventTagName(String eventTagName) {
        this.eventTagName = eventTagName;
    }

    public String getEventTagImagePath() {
        return eventTagImagePath;
    }

    public void setEventTagImagePath(String eventTagImagePath) {
        this.eventTagImagePath = eventTagImagePath;
    }

    public String getEventSeverityName() {
        return eventSeverityName;
    }

    public void setEventSeverityName(String eventSeverityName) {
        this.eventSeverityName = eventSeverityName;
    }

    public Integer getEventSeverityColor() {
        return eventSeverityColor;
    }

    public void setEventSeverityColor(Integer eventSeverityColor) {
        this.eventSeverityColor = eventSeverityColor;
    }

    public Double getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(Double eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public Double getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(Double eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

}
