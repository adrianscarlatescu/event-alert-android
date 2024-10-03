package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.request.SubscriptionRequest;
import com.as.eventalertandroid.net.model.request.SubscriptionStatusRequest;
import com.as.eventalertandroid.net.model.request.SubscriptionTokenRequest;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SubscriptionService {

    @HEAD("/api/subscriptions/{userId}/{deviceId}")
    CompletableFuture<Void> subscriptionExists(@Path("userId") Long userId,
                                               @Path("deviceId") String deviceId);

    @GET("/api/subscriptions/{userId}/{deviceId}")
    CompletableFuture<Subscription> getByUserIdAndDeviceId(@Path("userId") Long userId,
                                                           @Path("deviceId") String deviceId);

    @POST("/api/subscriptions")
    CompletableFuture<Subscription> subscribe(@Body SubscriptionRequest subscriptionRequest);

    @PUT("/api/subscriptions")
    CompletableFuture<Subscription> update(@Body SubscriptionRequest subscriptionRequest);

    @PATCH("/api/subscriptions/{userId}/{deviceId}/status")
    CompletableFuture<Subscription> updateStatus(@Path("userId") Long userId,
                                                 @Path("deviceId") String deviceId,
                                                 @Body SubscriptionStatusRequest subscriptionStatusRequest);

    @PATCH("/api/subscriptions/{deviceId}/token")
    CompletableFuture<Void> updateToken(@Path("deviceId") String deviceId,
                                        @Body SubscriptionTokenRequest subscriptionTokenRequest);

    @DELETE("/api/subscriptions/{userId}/{firebaseToken}")
    CompletableFuture<Void> unsubscribe(@Path("userId") Long userId,
                                        @Path("firebaseToken") String firebaseToken);

}
