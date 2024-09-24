package com.as.eventalertandroid.data.dao;

import com.as.eventalertandroid.data.model.SubscriptionEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SubscriptionDao {

    @Query("SELECT * FROM subscriptionentity WHERE userId = :userId")
    SubscriptionEntity findByUserId(Long userId);

    @Query("SELECT * FROM subscriptionentity")
    List<SubscriptionEntity> findAll();

    @Insert
    void insert(SubscriptionEntity subscription);

    @Update
    void update(SubscriptionEntity subscription);

    @Query("DELETE FROM subscriptionentity WHERE userId = :userId")
    void deleteByUserId(Long userId);

}
