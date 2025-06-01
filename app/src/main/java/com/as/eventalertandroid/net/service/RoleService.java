package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.RoleDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface RoleService {

    @GET("/api/roles")
    CompletableFuture<List<RoleDTO>> getRoles();

}
