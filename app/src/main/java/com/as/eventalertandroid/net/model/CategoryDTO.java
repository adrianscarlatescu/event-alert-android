package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryDTO implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("label")
    public String label;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("position")
    public Integer position;

}
