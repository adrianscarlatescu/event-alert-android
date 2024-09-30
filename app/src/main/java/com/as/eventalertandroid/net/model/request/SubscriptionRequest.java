package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class SubscriptionRequest {

    @SerializedName("userId")
    public Long userId;

    @SerializedName("deviceId")
    public String deviceId;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("radius")
    public Integer radius;

    @SerializedName("firebaseToken")
    public String firebaseToken;

}
