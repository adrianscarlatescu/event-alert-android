package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PageDTO<T> implements Serializable {

    @SerializedName("totalPages")
    public int totalPages;

    @SerializedName("totalElements")
    public long totalElements;

    @SerializedName("content")
    public List<T> content;

}
