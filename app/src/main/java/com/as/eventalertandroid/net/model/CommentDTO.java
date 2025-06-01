package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CommentDTO implements Serializable {

    @SerializedName("id")
    public Long id;

    @SerializedName("createdAt")
    public LocalDateTime createdAt;

    @SerializedName("modifiedAt")
    public LocalDateTime modifiedAt;

    @SerializedName("comment")
    public String comment;

    @SerializedName("user")
    public UserBaseDTO user;

}
