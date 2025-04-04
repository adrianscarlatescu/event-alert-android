package com.as.eventalertandroid.handler;

import android.content.Context;

import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.service.AuthService;
import com.as.eventalertandroid.net.service.SeverityService;
import com.as.eventalertandroid.net.service.StatusService;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.net.service.TypeService;
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
    private static final TypeService typeService = RetrofitClient.getInstance().create(TypeService.class);
    private static final SeverityService severityService = RetrofitClient.getInstance().create(SeverityService.class);
    private static final StatusService statusService = RetrofitClient.getInstance().create(StatusService.class);

    public static CompletableFuture<Void> runStartupSync(Context context) {
        return syncUserProfile()
                .thenCompose(aVoid -> syncSubscription(session.getUserId(), DeviceHandler.getAndroidId(context)))
                .thenCompose(aVoid -> syncTypes())
                .thenCompose(aVoid -> syncSeverities())
                .thenCompose(aVoid -> syncStatuses());
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

    private static CompletableFuture<Void> syncTypes() {
        return typeService.getTypes()
                .thenCompose(types -> {
                    session.setTypes(types);
                    List<String> imagePaths = types.stream()
                            .map(type -> type.imagePath)
                            .collect(Collectors.toList());
                    return fetchImages(imagePaths);
                });
    }

    private static CompletableFuture<Void> syncSeverities() {
        return severityService.getSeverities()
                .thenAccept(session::setSeverities);
    }

    private static CompletableFuture<Void> syncStatuses() {
        return statusService.getStatuses()
                .thenAccept(session::setStatuses);
    }

    private static CompletableFuture<Void> fetchImages(List<String> paths) {
        CompletableFuture<?>[] futures = paths.stream()
                .filter(path -> path != null && !path.isEmpty())
                .map(path -> {
                    CompletableFuture<Void> result = new CompletableFuture<>();
                    Picasso.get()
                            .load(Constants.BASE_URL + "api/images?path=" + path)
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
        return authService.refresh()
                .thenAccept(authTokens -> {
                    session.setAccessToken(authTokens.accessToken);
                    session.setRefreshToken(authTokens.refreshToken);
                })
                .exceptionally(throwable -> null);
    }

}
