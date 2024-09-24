package com.as.eventalertandroid.net;

import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.service.EventSeverityService;
import com.as.eventalertandroid.net.service.EventTagService;
import com.as.eventalertandroid.net.service.UserService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SyncHandler {


    public static CompletableFuture<Void> runStartupSync() {
        return syncUserProfile()
                .thenCompose(aVoid -> syncEventTags())
                .thenCompose(aVoid -> syncEventSeverities());
    }

    public static CompletableFuture<Void> syncUserProfile() {
        UserService ws = RetrofitClient.getInstance().create(UserService.class);
        return ws.getProfile()
                .thenAccept(Session.getInstance()::setUser);
    }

    public static CompletableFuture<Void> syncEventTags() {
        EventTagService ws = RetrofitClient.getInstance().create(EventTagService.class);
        return ws.getAll()
                .thenCompose(tags -> {
                    Session.getInstance().setTags(tags);
                    List<String> imagePaths = tags.stream()
                            .map(tag -> tag.imagePath)
                            .collect(Collectors.toList());
                    return fetchImages(imagePaths);
                });
    }

    public static CompletableFuture<Void> syncEventSeverities() {
        EventSeverityService ws = RetrofitClient.getInstance().create(EventSeverityService.class);
        return ws.getAll()
                .thenAccept(Session.getInstance()::setSeverities);
    }

    public static CompletableFuture<Void> fetchImages(List<String> paths) {
        CompletableFuture[] futures = paths.stream()
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

}
