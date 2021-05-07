package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.EventTag;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventTagService {

    @Headers("Authorization: Access Token")
    @GET("/api/tags")
    CompletableFuture<List<EventTag>> getAll();

    @Headers("Authorization: Access Token")
    @GET("/api/tags/{id}")
    CompletableFuture<EventTag> getById(@Path("id") Long id);

    @Headers("Authorization: Access Token")
    @POST("/api/tags")
    CompletableFuture<EventTag> save(@Body EventTag tag);

    @Headers("Authorization: Access Token")
    @PUT("/api/tags/{id}")
    CompletableFuture<EventTag> updateById(@Body EventTag tag, @Path("id") Long id);

    @Headers("Authorization: Access Token")
    @DELETE("/api/tags/{id}")
    CompletableFuture<Void> deleteById(@Path("id") Long id);

}
