package com.as.eventalertandroid.data.dao;

import com.as.eventalertandroid.data.model.EventNotificationEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface EventNotificationDao {

    @Query("SELECT * FROM eventnotificationentity WHERE userId = :userId ORDER BY id DESC LIMIT 100")
    List<EventNotificationEntity> findByUserId(Long userId);

    @Insert
    long insert(EventNotificationEntity eventNotification);

    @Insert
    void insert(List<EventNotificationEntity> eventNotifications);

    @Update
    void update(EventNotificationEntity eventNotification);

}
