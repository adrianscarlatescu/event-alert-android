package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

public class ApiFailureResponse {

    @SerializedName("errors")
    public ApiError[] errors;

}
