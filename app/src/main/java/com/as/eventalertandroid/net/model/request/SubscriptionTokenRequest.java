package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class SubscriptionTokenRequest {

    @SerializedName("deviceId")
    public String deviceId;

    @SerializedName("firebaseToken")
    public String firebaseToken;

}
