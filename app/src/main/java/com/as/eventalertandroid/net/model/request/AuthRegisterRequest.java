package com.as.eventalertandroid.net.model.request;

import com.google.gson.annotations.SerializedName;

public class AuthRegisterRequest {

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("confirmPassword")
    public String confirmPassword;

}
