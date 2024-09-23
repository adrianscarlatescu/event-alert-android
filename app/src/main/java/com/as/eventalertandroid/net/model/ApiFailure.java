package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

public class ApiFailure {

    @SerializedName("errors")
    public ApiError[] errors;

}
