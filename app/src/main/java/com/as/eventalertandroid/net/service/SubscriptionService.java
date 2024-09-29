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
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SubscriptionService {

    @Headers("Authorization: Access Token")
    @HEAD("/api/subscriptions/{userId}/{deviceId}")
    CompletableFuture<Void> subscriptionExists(@Path("userId") Long userId,
                                               @Path("deviceId") String deviceId);

    @Headers("Authorization: Access Token")
    @GET("/api/subscriptions/{userId}/{deviceId}")
    CompletableFuture<Subscription> getByUserIdAndDeviceId(@Path("userId") Long userId,
                                                           @Path("deviceId") String deviceId);

    @Headers("Authorization: Access Token")
    @POST("/api/subscriptions")
    CompletableFuture<Subscription> subscribe(@Body SubscriptionRequest subscriptionRequest);

    @Headers("Authorization: Access Token")
    @PUT("/api/subscriptions")
    CompletableFuture<Subscription> update(@Body SubscriptionRequest subscriptionRequest);

    @Headers("Authorization: Access Token")
    @PATCH("/api/subscriptions/{userId}/{deviceId}/status")
    CompletableFuture<Subscription> updateStatus(@Path("userId") Long userId,
                                                 @Path("deviceId") String deviceId,
                                                 @Body SubscriptionStatusRequest subscriptionStatusRequest);

    @Headers("Authorization: Access Token")
    @PATCH("/api/subscriptions/tokens")
    CompletableFuture<Void> updateTokens(@Body SubscriptionTokenRequest subscriptionTokenRequest);

    @Headers("Authorization: Access Token")
    @DELETE("/api/subscriptions/{userId}/{firebaseToken}")
    CompletableFuture<Void> unsubscribe(@Path("userId") Long userId,
                                        @Path("firebaseToken") String firebaseToken);

}
