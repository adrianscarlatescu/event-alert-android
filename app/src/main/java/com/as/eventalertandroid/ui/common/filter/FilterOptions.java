package com.as.eventalertandroid.ui.common.filter;

import java.time.LocalDate;
import java.util.Set;

public class FilterOptions {

    private Integer radius;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> typeIds;
    private Set<String> severityIds;
    private Set<String> statusIds;

    private FilterOptions() {
    }

    public FilterOptions(Integer radius,
                         LocalDate startDate,
                         LocalDate endDate,
                         Set<String> typeIds,
                         Set<String> severityIds,
                         Set<String> statusIds) {
        this.radius = radius;
        this.startDate = startDate;
        this.endDate = endDate;
        this.typeIds = typeIds;
        this.severityIds = severityIds;
        this.statusIds = statusIds;
    }

    public Integer getRadius() {
        return radius;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Set<String> getTypeIds() {
        return typeIds;
    }

    public Set<String> getSeverityIds() {
        return severityIds;
    }

    public Set<String> getStatusIds() {
        return statusIds;
    }

}
