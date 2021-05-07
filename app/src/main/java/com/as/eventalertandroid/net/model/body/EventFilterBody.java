package com.as.eventalertandroid.net.model.body;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class EventFilterBody {

    @SerializedName("radius")
    public int radius;
    @SerializedName("startDate")
    public LocalDate startDate;
    @SerializedName("endDate")
    public LocalDate endDate;
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("tagsIds")
    public long[] tagsIds;
    @SerializedName("severitiesIds")
    public long[] severitiesIds;

}
