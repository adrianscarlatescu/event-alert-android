package com.as.eventalertandroid.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SubscriptionEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long userId;
    private String firebaseToken;

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

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

}
