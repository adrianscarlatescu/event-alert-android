package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

public class Subscription {

    @SerializedName("id")
    public Long id;
    @SerializedName("user")
    public User user;
    @SerializedName("latitude")
    public Double latitude;
    @SerializedName("longitude")
    public Double longitude;
    @SerializedName("radius")
    public Integer radius;
    @SerializedName("deviceToken")
    public String deviceToken;

}
