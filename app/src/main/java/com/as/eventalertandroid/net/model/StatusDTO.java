package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusDTO implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("label")
    public String label;

    @SerializedName("color")
    public String color;

    @SerializedName("description")
    public String description;

    @SerializedName("position")
    public Integer position;

}
