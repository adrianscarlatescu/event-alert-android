package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.request.SubscriptionRequest;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SubscriptionService {

    @Headers("Authorization: Access Token")
    @GET("/api/subscriptions/{deviceToken}")
    CompletableFuture<Subscription> getByDeviceToken(@Path("deviceToken") String deviceToken);

    @Headers("Authorization: Access Token")
    @POST("/api/subscriptions")
    CompletableFuture<Subscription> subscribe(@Body SubscriptionRequest subscriptionRequest);

    @Headers("Authorization: Access Token")
    @PUT("/api/subscriptions")
    CompletableFuture<Subscription> update(@Body SubscriptionRequest subscriptionRequest);

    @Headers("Authorization: Access Token")
    @DELETE("/api/subscriptions/{deviceToken}")
    CompletableFuture<Void> unsubscribe(@Path("deviceToken") String deviceToken);

}
