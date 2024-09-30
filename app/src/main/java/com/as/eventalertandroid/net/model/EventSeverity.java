package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class EventSeverity {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("color")
    public int color;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof EventSeverity) {
            EventSeverity eventSeverity = (EventSeverity) obj;
            return id.longValue() == eventSeverity.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
