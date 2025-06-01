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
    private String createdAt;
    private String typeLabel;
    private String typeImagePath;
    private String severityLabel;
    private String severityColor;
    private String statusLabel;
    private String statusColor;
    private String impactRadius;
    private Double latitude;
    private Double longitude;

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getTypeImagePath() {
        return typeImagePath;
    }

    public void setTypeImagePath(String typeImagePath) {
        this.typeImagePath = typeImagePath;
    }

    public String getSeverityLabel() {
        return severityLabel;
    }

    public void setSeverityLabel(String severityLabel) {
        this.severityLabel = severityLabel;
    }

    public String getSeverityColor() {
        return severityColor;
    }

    public void setSeverityColor(String severityColor) {
        this.severityColor = severityColor;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getImpactRadius() {
        return impactRadius;
    }

    public void setImpactRadius(String impactRadius) {
        this.impactRadius = impactRadius;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
