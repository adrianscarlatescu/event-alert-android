package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class Subscription {

    @SerializedName("id")
    public Long id;

    @SerializedName("user")
    public UserBase user;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("radius")
    public Integer radius;

    @SerializedName("deviceId")
    public String deviceId;

    @SerializedName("firebaseToken")
    public String firebaseToken;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Subscription) {
            Subscription subscription = (Subscription) obj;
            return id.longValue() == subscription.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
