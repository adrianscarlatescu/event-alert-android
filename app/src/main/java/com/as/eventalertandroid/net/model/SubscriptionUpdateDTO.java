package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionUpdateDTO implements Serializable {

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("radius")
    public Integer radius;

    @SerializedName("firebaseToken")
    public String firebaseToken;

}
