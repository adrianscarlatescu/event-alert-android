package com.as.eventalertandroid.app;

import com.as.eventalertandroid.enums.Role;
import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.EventTag;
import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Session {

    private static Session instance;

    private String accessToken;
    private String refreshToken;

    private User user;
    private Subscription subscription;
    private List<EventTag> tags;
    private List<EventSeverity> severities;

    private Double userLatitude;
    private Double userLongitude;

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
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

    public boolean isUserAdmin() {
        return Stream.of(user.userRoles).anyMatch(userRole -> userRole.name == Role.ROLE_ADMIN);
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

    public boolean isUserLocationSet() {
        return userLatitude != null && userLongitude != null;
    }

}
