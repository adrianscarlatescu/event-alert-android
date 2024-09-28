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
    long insert(SubscriptionEntity subscription);

    @Update
    void update(SubscriptionEntity subscription);

    @Query("UPDATE subscriptionentity SET firebaseToken = :token")
    void updateFirebaseToken(String token);

    @Query("DELETE FROM subscriptionentity WHERE userId = :userId")
    void deleteByUserId(Long userId);

    @Query("DELETE FROM subscriptionentity")
    void deleteAll();

}
