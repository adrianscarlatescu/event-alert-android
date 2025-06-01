package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SeverityDTO implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("label")
    public String label;

    @SerializedName("color")
    public String color;

    @SerializedName("position")
    public String position;

}
