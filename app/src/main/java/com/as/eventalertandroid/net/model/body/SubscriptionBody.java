package com.as.eventalertandroid.net.model.body;

import com.google.gson.annotations.SerializedName;

public class SubscriptionBody {

    @SerializedName("latitude")
    public Double latitude;
    @SerializedName("longitude")
    public Double longitude;
    @SerializedName("radius")
    public Integer radius;
    @SerializedName("deviceToken")
    public String deviceToken;

}
