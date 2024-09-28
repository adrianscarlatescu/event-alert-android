package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class SubscriptionRequest {

    @SerializedName("userId")
    private Long userId;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("radius")
    public Integer radius;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("firebaseToken")
    public String firebaseToken;

}
