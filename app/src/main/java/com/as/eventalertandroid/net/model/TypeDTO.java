package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TypeDTO implements Serializable {

    @SerializedName("id")
    public Long id;

    @SerializedName("label")
    public String label;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("position")
    public Integer position;

    @SerializedName("category")
    public CategoryDTO category;

}
