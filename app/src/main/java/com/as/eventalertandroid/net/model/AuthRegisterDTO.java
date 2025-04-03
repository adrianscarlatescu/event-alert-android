package com.as.eventalertandroid.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthRegisterDTO implements Serializable {

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("confirmPassword")
    public String confirmPassword;

}
