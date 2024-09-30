package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class EventTag {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

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
        if (obj instanceof EventTag) {
            EventTag eventTag = (EventTag) obj;
            return id.longValue() == eventTag.id.longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
