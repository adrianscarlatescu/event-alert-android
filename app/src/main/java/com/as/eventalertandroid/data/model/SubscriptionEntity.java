package com.as.eventalertandroid.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SubscriptionEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long userId;
    private String deviceToken;

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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

}
