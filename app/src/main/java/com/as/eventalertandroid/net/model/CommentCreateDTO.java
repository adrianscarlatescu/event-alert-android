package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentCreateDTO implements Serializable {

    @SerializedName("comment")
    public String comment;

    @SerializedName("eventId")
    public Long eventId;

    @SerializedName("userId")
    public Long userId;

}
