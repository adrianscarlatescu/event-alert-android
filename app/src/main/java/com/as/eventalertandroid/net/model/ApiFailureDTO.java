package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiFailureDTO implements Serializable {

    @SerializedName("errors")
    public ApiErrorDTO[] errors;

}
