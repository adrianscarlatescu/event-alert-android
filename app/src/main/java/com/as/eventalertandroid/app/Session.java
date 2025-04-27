package com.as.eventalertandroid.app;

import com.as.eventalertandroid.enums.id.RoleId;
import com.as.eventalertandroid.net.model.CategoryDTO;
import com.as.eventalertandroid.net.model.OrderDTO;
import com.as.eventalertandroid.net.model.RoleDTO;
import com.as.eventalertandroid.net.model.SeverityDTO;
import com.as.eventalertandroid.net.model.StatusDTO;
import com.as.eventalertandroid.net.model.SubscriptionDTO;
import com.as.eventalertandroid.net.model.TypeDTO;
import com.as.eventalertandroid.net.model.UserDTO;

import java.util.List;
import java.util.stream.Stream;

public class Session {

    private static Session instance;

    private String accessToken;
    private String refreshToken;

    private List<RoleDTO> roles;
    private UserDTO user;
    private SubscriptionDTO subscription;
    private List<CategoryDTO> categories;
    private List<TypeDTO> types;
    private List<SeverityDTO> severities;
    private List<StatusDTO> statuses;
    private List<OrderDTO> orders;

    private Double userLatitude;
    private Double userLongitude;

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }

    public Long getUserId() {
        return user.id;
    }

    public void increaseUserReportsNumber() {
        user.reportsNumber++;
    }

    public boolean isUserAdmin() {
        return Stream.of(user.roles).anyMatch(role -> role.id == RoleId.ROLE_ADMIN);
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

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public List<TypeDTO> getTypes() {
        return types;
    }

    public void setTypes(List<TypeDTO> types) {
        this.types = types;
    }

    public List<SeverityDTO> getSeverities() {
        return severities;
    }

    public void setSeverities(List<SeverityDTO> severities) {
        this.severities = severities;
    }

    public List<StatusDTO> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<StatusDTO> statuses) {
        this.statuses = statuses;
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

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public void setSubscription(SubscriptionDTO subscription) {
        this.subscription = subscription;
    }

    public SubscriptionDTO getSubscription() {
        return subscription;
    }

    public boolean isUserLocationSet() {
        return userLatitude != null && userLongitude != null;
    }

}
