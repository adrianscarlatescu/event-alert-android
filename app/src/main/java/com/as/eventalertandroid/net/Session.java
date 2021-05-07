package com.as.eventalertandroid.net;

import android.os.Handler;

import com.as.eventalertandroid.enums.Role;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.AuthRefreshToken;
import com.as.eventalertandroid.net.model.AuthTokens;
import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.EventTag;
import com.as.eventalertandroid.net.model.User;
import com.as.eventalertandroid.net.service.AuthService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Session {

    private static Session session;

    private Handler handler = new Handler();

    private String accessToken;
    private String refreshToken;

    private User user;
    private List<EventTag> tags;
    private List<EventSeverity> severities;

    private Double latitude;
    private Double longitude;

    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isAdminUser() {
        return Stream.of(user.userRoles).anyMatch(userRole -> userRole.name == Role.ROLE_ADMIN);
    }

    public CompletableFuture<AuthRefreshToken> refreshToken() {
        AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        return authService.refreshToken()
                .thenApply(result -> {
                    this.accessToken = result.accessToken;
                    return result;
                })
                .exceptionally(throwable -> {
                    // TODO
                    return null;
                });
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
        this.tags.sort((o1, o2) -> o1.name.compareTo(o2.name));
    }

    public List<EventSeverity> getSeverities() {
        return severities;
    }

    public void setSeverities(List<EventSeverity> severities) {
        this.severities = severities;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Handler getHandler() {
        return handler;
    }

    public boolean isLocationSet() {
        return latitude != null && longitude != null;
    }

}
