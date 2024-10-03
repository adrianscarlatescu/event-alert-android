package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.User;
import com.as.eventalertandroid.net.model.request.UserRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("/api/users")
    CompletableFuture<List<User>> getAll();

    @GET("/api/users/{id}")
    CompletableFuture<User> getById(@Path("id") Long id);

    @PUT("/api/users/{id}")
    CompletableFuture<User> updateById(@Body UserRequest userRequest, @Path("id") Long id);

    @DELETE("/api/users/{id}")
    CompletableFuture<User> deleteById(@Path("id") Long id);

    @GET("/api/profile")
    CompletableFuture<User> getProfile();

    @PUT("/api/profile")
    CompletableFuture<User> updateProfile(@Body UserRequest userRequest);

}
