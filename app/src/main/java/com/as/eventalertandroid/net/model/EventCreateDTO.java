package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class EventCreateDTO implements Serializable {

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("impactRadius")
    private BigDecimal impactRadius;

    @SerializedName("typeId")
    public Long typeId;

    @SerializedName("severityId")
    public Long severityId;

    @SerializedName("statusId")
    public Long statusId;

    @SerializedName("userId")
    public Long userId;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("description")
    public String description;

}
