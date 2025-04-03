package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.id.GenderId;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenderDTO implements Serializable {

    @SerializedName("id")
    public GenderId id;

    @SerializedName("label")
    public String label;

    @SerializedName("position")
    public Integer position;

}
