package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.SubscriptionCreateDTO;
import com.as.eventalertandroid.net.model.SubscriptionDTO;
import com.as.eventalertandroid.net.model.SubscriptionStatusUpdateDTO;
import com.as.eventalertandroid.net.model.SubscriptionTokenUpdateDTO;
import com.as.eventalertandroid.net.model.SubscriptionUpdateDTO;

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
    CompletableFuture<SubscriptionDTO> getByUserIdAndDeviceId(@Path("userId") Long userId,
                                                              @Path("deviceId") String deviceId);

    @POST("/api/subscriptions")
    CompletableFuture<SubscriptionDTO> subscribe(@Body SubscriptionCreateDTO subscriptionCreate);

    @PUT("/api/subscriptions/{userId}/{deviceId}")
    CompletableFuture<SubscriptionDTO> update(@Path("userId") Long userId,
                                              @Path("deviceId") String deviceId,
                                              @Body SubscriptionUpdateDTO subscriptionUpdateDTO);

    @PATCH("/api/subscriptions/{userId}/{deviceId}/status")
    CompletableFuture<SubscriptionDTO> updateStatus(@Path("userId") Long userId,
                                                    @Path("deviceId") String deviceId,
                                                    @Body SubscriptionStatusUpdateDTO subscriptionStatusUpdate);

    @PATCH("/api/subscriptions/{deviceId}/token")
    CompletableFuture<Void> updateToken(@Path("deviceId") String deviceId,
                                        @Body SubscriptionTokenUpdateDTO subscriptionTokenUpdate);

    @DELETE("/api/subscriptions/{userId}/{firebaseToken}")
    CompletableFuture<Void> unsubscribe(@Path("userId") Long userId,
                                        @Path("firebaseToken") String firebaseToken);

}
