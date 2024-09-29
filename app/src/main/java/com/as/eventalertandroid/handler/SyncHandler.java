package com.as.eventalertandroid.handler;

import android.content.Context;

import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.service.AuthService;
import com.as.eventalertandroid.net.service.EventSeverityService;
import com.as.eventalertandroid.net.service.EventTagService;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.net.service.UserService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import retrofit2.HttpException;

public class SyncHandler {

    private static final Session session = Session.getInstance();
    private static final AuthService authService = RetrofitClient.getInstance().create(AuthService.class);
    private static final UserService userService = RetrofitClient.getInstance().create(UserService.class);
    private static final SubscriptionService subscriptionService = RetrofitClient.getInstance().create(SubscriptionService.class);
    private static final EventTagService tagService = RetrofitClient.getInstance().create(EventTagService.class);
    private static final EventSeverityService severityService = RetrofitClient.getInstance().create(EventSeverityService.class);

    public static CompletableFuture<Void> runStartupSync(Context context) {
        return syncUserProfile()
                .thenCompose(aVoid -> syncSubscription(session.getUserId(), DeviceHandler.getAndroidId(context)))
                .thenCompose(aVoid -> syncEventTags())
                .thenCompose(aVoid -> syncEventSeverities());
    }

    private static CompletableFuture<Void> syncUserProfile() {
        return userService.getProfile()
                .thenAccept(session::setUser);
    }

    private static CompletableFuture<Void> syncSubscription(Long userId, String deviceId) {
        return subscriptionService.subscriptionExists(userId, deviceId)
                .handle((identifier, throwable) -> {
                    if (throwable == null) {
                        return true;
                    }
                    if (throwable instanceof HttpException) {
                        HttpException httpException = (HttpException) throwable;
                        if (httpException.code() == 404) {
                            return false;
                        }
                        throw httpException;
                    }
                    throw new CompletionException(throwable);
                })
                .thenCompose(exists -> {
                    if (exists) {
                        return subscriptionService.getByUserIdAndDeviceId(userId, deviceId);
                    }
                    return CompletableFuture.completedFuture(null);
                })
                .thenAccept(session::setSubscription);
    }

    private static CompletableFuture<Void> syncEventTags() {
        return tagService.getAll()
                .thenCompose(tags -> {
                    session.setTags(tags);
                    List<String> imagePaths = tags.stream()
                            .map(tag -> tag.imagePath)
                            .collect(Collectors.toList());
                    return fetchImages(imagePaths);
                });
    }

    private static CompletableFuture<Void> syncEventSeverities() {
        return severityService.getAll()
                .thenAccept(session::setSeverities);
    }

    private static CompletableFuture<Void> fetchImages(List<String> paths) {
        CompletableFuture<?>[] futures = paths.stream()
                .filter(path -> path != null && !path.isEmpty())
                .map(path -> {
                    CompletableFuture<Void> result = new CompletableFuture<>();
                    Picasso.get()
                            .load(Constants.BASE_URL + "api/image?path=" + path)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .fetch(new Callback() {
                                @Override
                                public void onSuccess() {
                                    result.complete(null);
                                }

                                @Override
                                public void onError(Exception e) {
                                    result.completeExceptionally(e);
                                }
                            });
                    return result;
                })
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    public static CompletableFuture<Void> refreshToken() {
        return authService.refreshToken()
                .thenAccept(authTokens -> {
                    session.setAccessToken(authTokens.accessToken);
                    session.setRefreshToken(authTokens.refreshToken);
                });
    }

}
