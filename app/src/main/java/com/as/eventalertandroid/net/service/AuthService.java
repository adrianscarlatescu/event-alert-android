package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.AuthTokensDTO;
import com.as.eventalertandroid.net.model.UserDTO;
import com.as.eventalertandroid.net.model.AuthLoginDTO;
import com.as.eventalertandroid.net.model.AuthRegisterDTO;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/auth/register")
    CompletableFuture<UserDTO> register(@Body AuthRegisterDTO authRegisterDTO);

    @POST("/api/auth/login")
    CompletableFuture<AuthTokensDTO> login(@Body AuthLoginDTO authLoginDTO);

    @GET("/api/auth/refresh")
    CompletableFuture<AuthTokensDTO> refresh();

    @POST("/api/auth/logout")
    CompletableFuture<Void> logout();

}
