package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.Gender;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalDateTime;

import androidx.annotation.Nullable;

public class User {

    @SerializedName("id")
    public Long id;

    @SerializedName("email")
    public String email;

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

    @SerializedName("userRoles")
    public UserRole[] userRoles;

    @SerializedName("joinDateTime")
    public LocalDateTime joinDateTime;

    @SerializedName("reportsNumber")
    public Integer reportsNumber;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof User) {
            User user = (User) obj;
            return id.longValue() == user.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
