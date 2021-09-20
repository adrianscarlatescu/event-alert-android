package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.body.SubscriptionBody;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SubscriptionService {

    @Headers("Authorization: Access Token")
    @GET("/api/subscriptions")
    CompletableFuture<Subscription> getByDeviceToken(@Query("deviceToken") String deviceToken);

    @Headers("Authorization: Access Token")
    @POST("/api/subscriptions")
    CompletableFuture<Subscription> subscribe(@Body SubscriptionBody body);

    @Headers("Authorization: Access Token")
    @PUT("/api/subscriptions")
    CompletableFuture<Subscription> update(@Body SubscriptionBody body);

    @Headers("Authorization: Access Token")
    @DELETE("/api/subscriptions")
    CompletableFuture<Void> unsubscribe(@Query("deviceToken") String deviceToken);

}
