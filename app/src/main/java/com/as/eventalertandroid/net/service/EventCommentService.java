package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.EventComment;
import com.as.eventalertandroid.net.model.request.EventCommentRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventCommentService {

    @Headers("Authorization: Access Token")
    @GET("/api/comments/{eventId}")
    CompletableFuture<List<EventComment>> getByEventId(@Path("eventId") Long id);

    @Headers("Authorization: Access Token")
    @POST("/api/comments")
    CompletableFuture<EventComment> save(@Body EventCommentRequest commentRequest);

}
