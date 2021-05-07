package com.as.eventalertandroid.net.model.body;

import com.google.gson.annotations.SerializedName;

public class EventCommentBody {

    @SerializedName("comment")
    public String comment;
    @SerializedName("eventId")
    public Long eventId;
    @SerializedName("userId")
    public Long userId;

}
