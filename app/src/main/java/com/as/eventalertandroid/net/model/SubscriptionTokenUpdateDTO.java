package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionTokenUpdateDTO implements Serializable {

    @SerializedName("firebaseToken")
    public String firebaseToken;

}
