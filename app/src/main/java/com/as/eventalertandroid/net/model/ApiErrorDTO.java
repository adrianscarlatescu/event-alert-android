package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiErrorDTO implements Serializable {

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;

}
