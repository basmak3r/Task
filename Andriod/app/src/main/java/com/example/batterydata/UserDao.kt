package com.example.batterydata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM BatteryData")
    fun getAll(): List<User>


    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}