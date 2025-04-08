package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.GenderDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface GenderService {

    @GET("/api/genders")
    CompletableFuture<List<GenderDTO>> getGenders();

}
