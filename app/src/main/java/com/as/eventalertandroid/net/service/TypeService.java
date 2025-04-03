package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.TypeDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface TypeService {

    @GET("/api/tags")
    CompletableFuture<List<TypeDTO>> getTypes();

}
