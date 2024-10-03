package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.AuthTokens;
import com.as.eventalertandroid.net.model.User;
import com.as.eventalertandroid.net.model.request.AuthLoginRequest;
import com.as.eventalertandroid.net.model.request.AuthRegisterRequest;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/auth/register")
    CompletableFuture<User> register(@Body AuthRegisterRequest registerRequest);

    @POST("/api/auth/login")
    CompletableFuture<AuthTokens> login(@Body AuthLoginRequest loginRequest);

    @GET("/api/auth/refresh")
    CompletableFuture<AuthTokens> refreshToken();

    @POST("/api/auth/logout")
    CompletableFuture<Void> logout();

}
