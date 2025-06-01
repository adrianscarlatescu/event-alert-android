package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.id.RoleId;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoleDTO implements Serializable {

    @SerializedName("id")
    public RoleId id;

    @SerializedName("label")
    public String label;

    @SerializedName("description")
    public String description;

    @SerializedName("position")
    public Integer position;

}
