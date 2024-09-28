package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class SubscriptionStatusRequest {

    @SerializedName("userId")
    public Long userId;

    @SerializedName("deviceId")
    public String deviceId;

    @SerializedName("isActive")
    public Boolean isActive;

}
