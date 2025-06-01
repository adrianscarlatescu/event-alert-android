package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.SeverityDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface SeverityService {

    @GET("/api/severities")
    CompletableFuture<List<SeverityDTO>> getSeverities();

}
