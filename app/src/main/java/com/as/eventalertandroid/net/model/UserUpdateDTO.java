package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.id.RoleId;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class UserUpdateDTO implements Serializable {

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("dateOfBirth")
    public LocalDate dateOfBirth;

    @SerializedName("phoneNumber")
    public String phoneNumber;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("roleIds")
    public Set<RoleId> roleIds;

}
