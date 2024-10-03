package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.EventTag;
import com.as.eventalertandroid.net.model.request.EventTagRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventTagService {

    @GET("/api/tags")
    CompletableFuture<List<EventTag>> getAll();

    @GET("/api/tags/{id}")
    CompletableFuture<EventTag> getById(@Path("id") Long id);

    @POST("/api/tags")
    CompletableFuture<EventTag> save(@Body EventTagRequest tagRequest);

    @PUT("/api/tags/{id}")
    CompletableFuture<EventTag> updateById(@Body EventTagRequest tagRequest, @Path("id") Long id);

    @DELETE("/api/tags/{id}")
    CompletableFuture<Void> deleteById(@Path("id") Long id);

}
