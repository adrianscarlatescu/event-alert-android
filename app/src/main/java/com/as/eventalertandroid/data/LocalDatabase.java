package com.as.eventalertandroid.data;

import android.content.Context;

import com.as.eventalertandroid.data.dao.EventNotificationDao;
import com.as.eventalertandroid.data.dao.SubscriptionDao;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.data.model.SubscriptionEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SubscriptionEntity.class, EventNotificationEntity.class}, exportSchema = false, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    private static final String DB_NAME = "EventAlert.db";

    private static LocalDatabase instance;

    public static void init(Context context) {
        instance = Room.databaseBuilder(context, LocalDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public static LocalDatabase getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("The database must be initialized");
        }
        return instance;
    }

    public abstract SubscriptionDao subscriptionDao();

    public abstract EventNotificationDao eventNotificationDao();

}
