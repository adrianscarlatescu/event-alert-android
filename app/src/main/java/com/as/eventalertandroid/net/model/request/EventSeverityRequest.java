package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class EventSeverityRequest {

    @SerializedName("name")
    public String name;

    @SerializedName("color")
    public int color;

}
