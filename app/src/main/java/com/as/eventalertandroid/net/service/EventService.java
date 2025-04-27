package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.enums.id.OrderId;
import com.as.eventalertandroid.net.model.EventCreateDTO;
import com.as.eventalertandroid.net.model.EventDTO;
import com.as.eventalertandroid.net.model.EventFilterDTO;
import com.as.eventalertandroid.net.model.PageDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventService {

    @GET("/api/events/{id}")
    CompletableFuture<EventDTO> getEventById(@Path("id") Long id);

    @GET("/api/events")
    CompletableFuture<List<EventDTO>> getEventsByUserId(@Query("userId") Long userId);

    @POST("/api/events")
    CompletableFuture<EventDTO> postEvent(@Body EventCreateDTO eventCreateDTO);

    @POST("/api/events/filter")
    CompletableFuture<PageDTO<EventDTO>> getEventsByFilter(@Body EventFilterDTO eventFilterDTO,
                                                           @Query("pageSize") int pageSize,
                                                           @Query("pageNumber") int pageNumber,
                                                           @Query("orderId") OrderId orderId);

}
