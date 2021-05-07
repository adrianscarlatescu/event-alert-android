package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @Headers("Authorization: Access Token")
    @GET("/api/users")
    CompletableFuture<List<User>> getAll();

    @Headers("Authorization: Access Token")
    @GET("/api/users/{id}")
    CompletableFuture<User> getById(@Path("id") Long id);

    @Headers("Authorization: Access Token")
    @PUT("/api/users/{id}")
    CompletableFuture<User> updateById(@Body User user, @Path("id") Long id);

    @Headers("Authorization: Access Token")
    @DELETE("/api/users/{id}")
    CompletableFuture<User> deleteById(@Path("id") Long id);

    @Headers("Authorization: Access Token")
    @GET("/api/profile")
    CompletableFuture<User> getProfile();

    @Headers("Authorization: Access Token")
    @PUT("/api/profile")
    CompletableFuture<User> updateProfile(@Body User user);

}
