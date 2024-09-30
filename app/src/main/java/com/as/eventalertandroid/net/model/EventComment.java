package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import androidx.annotation.Nullable;

public class EventComment {

    @SerializedName("id")
    public Long id;

    @SerializedName("dateTime")
    public LocalDateTime dateTime;

    @SerializedName("comment")
    public String comment;

    @SerializedName("user")
    public UserBase user;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof EventComment) {
            EventComment comment = (EventComment) obj;
            return id.longValue() == comment.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
