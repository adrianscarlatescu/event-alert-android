package com.as.eventalertandroid.net;

import android.os.Handler;
import android.os.Looper;

import com.as.eventalertandroid.enums.Role;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.AuthTokens;
import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.EventTag;
import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.User;
import com.as.eventalertandroid.net.service.AuthService;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Session {

    private static Session instance;

    private Handler handler;

    private String accessToken;
    private String refreshToken;

    private User user;
    private Double userLatitude;
    private Double userLongitude;

    private List<EventTag> tags;
    private List<EventSeverity> severities;

    private Subscription subscription;

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void initHandler(Looper looper) {
        this.handler = new Handler(looper);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.id;
    }

    public void increaseUserReportsNumber() {
        user.reportsNumber++;
    }

    public boolean isAdminUser() {
        return Stream.of(user.userRoles).anyMatch(userRole -> userRole.name == Role.ROLE_ADMIN);
    }

    public CompletableFuture<AuthTokens> refreshToken() {
        AuthService ws = RetrofitClient.getInstance().create(AuthService.class);
        return ws.refreshToken()
                .thenApply(result -> {
                    setAuthTokens(result);
                    return result;
                })
                .exceptionally(throwable -> null);
    }

    public void setAuthTokens(AuthTokens authTokens) {
        this.accessToken = authTokens.accessToken;
        this.refreshToken = authTokens.refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<EventTag> getTags() {
        return tags;
    }

    public void setTags(List<EventTag> tags) {
        this.tags = tags;
        this.tags.sort(Comparator.comparing(o -> o.name));
    }

    public List<EventSeverity> getSeverities() {
        return severities;
    }

    public void setSeverities(List<EventSeverity> severities) {
        this.severities = severities;
    }

    public Double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(Double latitude) {
        this.userLatitude = latitude;
    }

    public Double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(Double longitude) {
        this.userLongitude = longitude;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public Handler getHandler() {
        return handler;
    }

    public boolean isUserLocationSet() {
        return userLatitude != null && userLongitude != null;
    }

}
