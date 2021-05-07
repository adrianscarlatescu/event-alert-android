package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import androidx.annotation.Nullable;

public class Event {

    @SerializedName("id")
    public Long id;
    @SerializedName("dateTime")
    public LocalDateTime dateTime;
    @SerializedName("latitude")
    public Double latitude;
    @SerializedName("longitude")
    public Double longitude;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("description")
    public String description;
    @SerializedName("severity")
    public EventSeverity severity;
    @SerializedName("tag")
    public EventTag tag;
    @SerializedName("user")
    public User user;
    @SerializedName("distance")
    public double distance;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Event) {
            Event event = (Event) obj;
            return id.longValue() == event.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
