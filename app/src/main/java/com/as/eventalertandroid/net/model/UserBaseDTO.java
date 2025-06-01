package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserBaseDTO implements Serializable {

    @SerializedName("id")
    public Long id;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("imagePath")
    public String imagePath;

}
