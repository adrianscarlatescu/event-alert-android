package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.CommentDTO;
import com.as.eventalertandroid.net.model.CommentCreateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentService {

    @GET("/api/comments/{eventId}")
    CompletableFuture<List<CommentDTO>> getCommentsByEventId(@Path("eventId") Long id);

    @POST("/api/comments")
    CompletableFuture<CommentDTO> postComment(@Body CommentCreateDTO commentCreateDTO);

}
