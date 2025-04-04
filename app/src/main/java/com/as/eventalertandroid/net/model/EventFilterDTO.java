package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class EventFilterDTO implements Serializable {

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

    @SerializedName("typeIds")
    public Set<String> typeIds;

    @SerializedName("severityIds")
    public Set<String> severityIds;

    @SerializedName("statusIds")
    public Set<String> statusIds;

}
