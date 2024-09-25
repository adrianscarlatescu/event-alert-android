package com.as.eventalertandroid.net.service;

import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.request.SubscriptionRequest;
import com.as.eventalertandroid.net.model.request.SubscriptionStatusRequest;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SubscriptionService {

    @Headers("Authorization: Access Token")
    @GET("/api/subscriptions/{firebaseToken}")
    CompletableFuture<Subscription> getByFirebaseToken(@Path("firebaseToken") String firebaseToken);

    @Headers("Authorization: Access Token")
    @POST("/api/subscriptions")
    CompletableFuture<Subscription> subscribe(@Body SubscriptionRequest subscriptionRequest);

    @Headers("Authorization: Access Token")
    @PUT("/api/subscriptions")
    CompletableFuture<Subscription> update(@Body SubscriptionRequest subscriptionRequest);


    @Headers("Authorization: Access Token")
    @PATCH("/api/subscriptions")
    CompletableFuture<Subscription> updateStatus(@Body SubscriptionStatusRequest subscriptionStatusRequest);

    @Headers("Authorization: Access Token")
    @DELETE("/api/subscriptions/{firebaseToken}")
    CompletableFuture<Void> unsubscribe(@Path("firebaseToken") String firebaseToken);

}
