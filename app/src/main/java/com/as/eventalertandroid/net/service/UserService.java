package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.UserDTO;
import com.as.eventalertandroid.net.model.UserUpdateDTO;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserService {

    @GET("/api/profile")
    CompletableFuture<UserDTO> getProfile();

    @PUT("/api/profile")
    CompletableFuture<UserDTO> putProfile(@Body UserUpdateDTO userUpdate);

}
