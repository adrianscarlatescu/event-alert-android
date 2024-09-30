package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class UserBase {

    @SerializedName("id")
    public Long id;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("imagePath")
    public String imagePath;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof UserBase) {
            UserBase userBase = (UserBase) obj;
            return id.longValue() == userBase.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
