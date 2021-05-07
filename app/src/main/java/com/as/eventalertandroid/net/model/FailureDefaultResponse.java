package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FailureDefaultResponse {

    @SerializedName("timestamp")
    public Date timestamp;
    @SerializedName("status")
    public int status;
    @SerializedName("error")
    public String error;
    @SerializedName("message")
    public String message;
    @SerializedName("path")
    public String path;

}
