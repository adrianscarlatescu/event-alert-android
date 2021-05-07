package com.as.eventalertandroid.ui.main.home.filter;

import com.as.eventalertandroid.net.model.EventSeverity;
import com.as.eventalertandroid.net.model.EventTag;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FilterOptions {

    private int radius;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<EventTag> tags;
    private Set<EventSeverity> severities;

    public FilterOptions() {
        radius = 100;
        endDate = LocalDate.now();
        startDate = LocalDate.of(endDate.getYear() - 1, endDate.getMonthValue(), endDate.getDayOfMonth());
        tags = new HashSet<>();
        severities = new HashSet<>();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<EventTag> getTags() {
        return tags;
    }

    public void setTags(Set<EventTag> tags) {
        this.tags = tags;
    }

    public Set<EventSeverity> getSeverities() {
        return severities;
    }

    public void setSeverities(Set<EventSeverity> severities) {
        this.severities = severities;
    }

}
