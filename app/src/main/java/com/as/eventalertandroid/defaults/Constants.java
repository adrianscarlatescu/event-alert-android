package com.as.eventalertandroid.defaults;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter defaultDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
    public static final DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final String BASE_URL = "http://192.168.1.9:8081/";
    public static final String LOGIN_URL_REGEX = ".*\\/auth\\/login$";
    public static final String REGISTER_URL_REGEX = ".*\\/auth\\/register$";
    public static final String REFRESH_URL_REGEX = ".*\\/auth\\/refresh$";
    public static final String SUBSCRIPTION_TOKEN_URL_REGEX = ".*\\/subscriptions\\/.*\\/token$";

    public static final String SHARED_PREF = "SharedPref";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    public static final String USER_ID = "UserId";
    public static final String USER_EMAIL = "UserEmail";
    public static final String USER_PASSWORD = "UserPassword";
    public static final String EVENT_NOTIFICATION_HANDLE = "NotificationHandle";

    public static final String PHONE_NUMBER_REGEX = "^[- +()0-9]{10,15}$";
    public static final String IMPACT_RADIUS_REGEX = "^\\d*(.\\d{1,2})?$";

    public static final int LENGTH_8 = 8;
    public static final int LENGTH_50 = 50;
    public static final int LENGTH_1000 = 1000;

    public static final int MIN_RADIUS = 1;
    public static final int MAX_RADIUS = 10_000;
    public static final int MAX_YEARS_INTERVAL = 2;
    public static final int PAGE_SIZE = 10;

    public static final BigDecimal MIN_IMPACT_RADIUS = new BigDecimal(0);
    public static final BigDecimal MAX_IMPACT_RADIUS = new BigDecimal(1000);

    public static final String IMAGE_EVENT_FILENAME = "event_";
    public static final String IMAGE_USER_FILENAME = "user_";

}
