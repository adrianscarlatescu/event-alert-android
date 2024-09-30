package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class EventCommentRequest {

    @SerializedName("comment")
    public String comment;

    @SerializedName("eventId")
    public Long eventId;

    @SerializedName("userId")
    public Long userId;

}
