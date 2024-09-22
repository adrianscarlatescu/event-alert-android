package com.as.eventalertandroid.net.model.request;

import com.as.eventalertandroid.enums.Gender;
import com.as.eventalertandroid.enums.Role;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Set;

public class UserRequest {

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

    @SerializedName("gender")
    public Gender gender;

    @SerializedName("roles")
    public Set<Role> roles;

}
