package com.as.eventalertandroid.net.model;

import com.as.eventalertandroid.enums.Role;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class UserRole {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public Role name;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof UserRole) {
            UserRole userRole = (UserRole) obj;
            return id.longValue() == userRole.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
