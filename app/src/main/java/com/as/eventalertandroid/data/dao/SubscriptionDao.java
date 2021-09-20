package com.as.eventalertandroid.data.dao;

import com.as.eventalertandroid.data.model.SubscriptionEntity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SubscriptionDao {

    @Query("SELECT * FROM subscriptionentity WHERE userId = :userId")
    SubscriptionEntity findByUserId(Long userId);

    @Insert
    void insert(SubscriptionEntity subscription);

    @Update
    void update(SubscriptionEntity subscription);

    @Query("DELETE FROM subscriptionentity WHERE userId = :userId")
    void deleteByUserId(Long userId);

}
