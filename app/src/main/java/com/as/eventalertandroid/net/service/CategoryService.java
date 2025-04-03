package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.CategoryDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;

public interface CategoryService {

    @GET("/api/categories")
    CompletableFuture<List<CategoryDTO>> getCategories();

}
