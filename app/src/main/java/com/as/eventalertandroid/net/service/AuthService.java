package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.AuthRefreshToken;
import com.as.eventalertandroid.net.model.AuthTokens;
import com.as.eventalertandroid.net.model.User;
import com.as.eventalertandroid.net.model.body.AuthLoginBody;
import com.as.eventalertandroid.net.model.body.AuthRegisterBody;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/auth/register")
    CompletableFuture<User> register(@Body AuthRegisterBody body);

    @POST("/api/auth/login")
    CompletableFuture<AuthTokens> login(@Body AuthLoginBody body);

    @Headers("Authorization: Refresh Token")
    @GET("/api/auth/refresh")
    CompletableFuture<AuthRefreshToken> refreshToken();

    @Headers("Authorization: Access Token")
    @POST("/api/auth/logout")
    CompletableFuture<Void> logout();

}
