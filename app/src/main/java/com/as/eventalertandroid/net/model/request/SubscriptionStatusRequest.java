package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class SubscriptionStatusRequest {

    @SerializedName("firebaseToken")
    public String firebaseToken;

    @SerializedName("isActive")
    public Boolean isActive;

}
