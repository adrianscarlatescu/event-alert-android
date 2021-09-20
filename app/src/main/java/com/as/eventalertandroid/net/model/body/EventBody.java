package com.as.eventalertandroid.net.model.body;

import com.google.gson.annotations.SerializedName;

public class EventBody {

    @SerializedName("latitude")
    public Double latitude;
    @SerializedName("longitude")
    public Double longitude;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("severityId")
    public Long severityId;
    @SerializedName("tagId")
    public Long tagId;
    @SerializedName("userId")
    public Long userId;
    @SerializedName("description")
    public String description;

}
