package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDTO implements Serializable {

    @SerializedName("id")
    public Long id;

    @SerializedName("joinedAt")
    public LocalDateTime joinedAt;

    @SerializedName("modifiedAt")
    public LocalDateTime modifiedAt;

    @SerializedName("email")
    public String email;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("gender")
    public GenderDTO gender;

    @SerializedName("dateOfBirth")
    public LocalDate dateOfBirth;

    @SerializedName("phoneNumber")
    public String phoneNumber;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("roles")
    public RoleDTO[] roles;

    @SerializedName("reportsNumber")
    public Integer reportsNumber;

}
