package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageResponse<T> {

    @SerializedName("totalPages")
    public int totalPages;

    @SerializedName("totalElements")
    public long totalElements;

    @SerializedName("content")
    public List<T> content;

}
