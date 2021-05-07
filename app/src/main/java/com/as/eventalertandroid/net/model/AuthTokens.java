package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

public class AuthTokens {

    @SerializedName("accessToken")
    public String accessToken;
    @SerializedName("refreshToken")
    public String refreshToken;

}
