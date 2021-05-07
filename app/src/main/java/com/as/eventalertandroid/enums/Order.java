package com.as.eventalertandroid.enums;

import com.as.eventalertandroid.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public enum Order {

    BY_DATE_ASCENDING(
            R.string.order_by_date_ascending,
            R.drawable.icon_calendar,
            R.drawable.icon_arrow_up),
    BY_DATE_DESCENDING(
            R.string.order_by_date_descending,
            R.drawable.icon_calendar,
            R.drawable.icon_arrow_down),
    BY_SEVERITY_ASCENDING(
            R.string.order_by_severity_ascending,
            R.drawable.icon_severity,
            R.drawable.icon_arrow_up),
    BY_SEVERITY_DESCENDING(
            R.string.order_by_severity_descending,
            R.drawable.icon_severity,
            R.drawable.icon_arrow_down),
    BY_DISTANCE_ASCENDING(
            R.string.order_by_distance_ascending,
            R.drawable.icon_distance,
            R.drawable.icon_arrow_up),
    BY_DISTANCE_DESCENDING(
            R.string.order_by_distance_descending,
            R.drawable.icon_distance,
            R.drawable.icon_arrow_down);

    @StringRes
    int name;
    @DrawableRes
    int icon;
    @DrawableRes
    int arrow;

    Order(@StringRes int name, @DrawableRes int icon, @DrawableRes int arrow) {
        this.name = name;
        this.icon = icon;
        this.arrow = arrow;
    }

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getArrow() {
        return arrow;
    }

}
