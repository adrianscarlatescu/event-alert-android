package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.request.EventSeverityRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventSeverityService {

    @GET("/api/severities")
    CompletableFuture<List<EventSeverity>> getAll();

    @GET("/api/severities/{id}")
    CompletableFuture<EventSeverity> getById(@Path("id") Long id);

    @POST("/api/severities")
    CompletableFuture<EventSeverity> save(@Body EventSeverityRequest severityRequest);

    @PUT("/api/severities/{id}")
    CompletableFuture<EventSeverity> updateById(@Body EventSeverityRequest severityRequest, @Path("id") Long id);

    @DELETE("/api/severities/{id}")
    CompletableFuture<Void> deleteById(@Path("id") Long id);

}
