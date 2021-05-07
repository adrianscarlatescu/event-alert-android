package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.Role;
import com.google.gson.annotations.SerializedName;

public class UserRole {

    @SerializedName("id")
    public Long id;
    @SerializedName("name")
    public Role name;

}
