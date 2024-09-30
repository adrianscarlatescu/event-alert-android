package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Set;

public class EventFilterRequest {

    @SerializedName("radius")
    public Integer radius;

    @SerializedName("startDate")
    public LocalDate startDate;

    @SerializedName("endDate")
    public LocalDate endDate;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("tagsIds")
    public Set<Long> tagsIds;

    @SerializedName("severitiesIds")
    public Set<Long> severitiesIds;

}
