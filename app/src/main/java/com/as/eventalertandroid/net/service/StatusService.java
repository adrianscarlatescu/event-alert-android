package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.StatusDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface StatusService {

    @GET("/api/statuses")
    CompletableFuture<List<StatusDTO>> getStatuses();

}
