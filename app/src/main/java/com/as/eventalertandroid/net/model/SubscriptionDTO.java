package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionDTO implements Serializable {

    @SerializedName("id")
    public Long id;

    @SerializedName("user")
    public UserBaseDTO user;

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

}
