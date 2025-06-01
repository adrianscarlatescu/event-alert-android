package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.id.OrderId;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDTO implements Serializable {

    @SerializedName("id")
    public OrderId id;

    @SerializedName("label")
    public String label;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("position")
    public Integer position;

}
