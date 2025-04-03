package com.as.eventalertandroid.ui.main.home.filter;

import com.as.eventalertandroid.net.model.SeverityDTO;
import com.as.eventalertandroid.net.model.TypeDTO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FilterOptions {

    private int radius;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<TypeDTO> types;
    private Set<SeverityDTO> severities;

    public FilterOptions() {
        radius = 1000;
        endDate = LocalDate.of(2020, 12, 31);
        startDate = LocalDate.of(2020, 1, 1);
        types = new HashSet<>();
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

    public Set<TypeDTO> getTags() {
        return types;
    }

    public void setTags(Set<TypeDTO> types) {
        this.types = types;
    }

    public Set<SeverityDTO> getSeverities() {
        return severities;
    }

    public void setSeverities(Set<SeverityDTO> severities) {
        this.severities = severities;
    }

}
