package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.OrderDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface OrderService {

    @GET("/api/orders")
    CompletableFuture<List<OrderDTO>> getOrders();

}
