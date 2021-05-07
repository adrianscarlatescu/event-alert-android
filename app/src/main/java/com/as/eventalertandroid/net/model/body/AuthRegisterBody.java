package com.as.eventalertandroid.net.model.body;

import com.google.gson.annotations.SerializedName;

public class AuthRegisterBody {

    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("confirmPassword")
    public String confirmPassword;

}
