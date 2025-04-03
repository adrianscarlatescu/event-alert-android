package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventDTO implements Serializable {

    @SerializedName("id")
    public Long id;

    @SerializedName("createdAt")
    public LocalDateTime createdAt;

    @SerializedName("modifiedAt")
    public LocalDateTime modifiedAt;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("impactRadius")
    public BigDecimal impactRadius;

    @SerializedName("type")
    public TypeDTO type;

    @SerializedName("severity")
    public SeverityDTO severity;

    @SerializedName("status")
    public StatusDTO status;

    @SerializedName("user")
    public UserBaseDTO user;

    @SerializedName("imagePath")
    public String imagePath;

    @SerializedName("description")
    public String description;

    @SerializedName("distance")
    public Double distance;

}
