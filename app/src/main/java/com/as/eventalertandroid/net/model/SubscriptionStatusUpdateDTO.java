package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionStatusUpdateDTO implements Serializable {

    @SerializedName("isActive")
    public Boolean isActive;

}
