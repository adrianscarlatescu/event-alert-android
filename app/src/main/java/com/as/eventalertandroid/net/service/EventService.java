package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.enums.Order;
import com.as.eventalertandroid.net.model.Event;
import com.as.eventalertandroid.net.model.PageResponse;
import com.as.eventalertandroid.net.model.request.EventFilterRequest;
import com.as.eventalertandroid.net.model.request.EventRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {

    @POST("/api/events/filter")
    CompletableFuture<PageResponse<Event>> getByFilter(@Body EventFilterRequest filterRequest,
                                                       @Query("pageSize") int pageSize,
                                                       @Query("pageNumber") int pageNumber,
                                                       @Query("order") Order order);

    @GET("/api/events")
    CompletableFuture<List<Event>> getByUserId(@Query("userId") Long userId);

    @GET("/api/events/{id}")
    CompletableFuture<Event> getById(@Path("id") Long id);

    @POST("/api/events")
    CompletableFuture<Event> save(@Body EventRequest eventRequest);

    @PUT("/api/events/{id}")
    CompletableFuture<Event> updateById(@Body EventRequest eventRequest, @Path("id") Long id);

    @DELETE("/api/events/{id}")
    CompletableFuture<Void> deleteById(@Path("id") Long id);

}
