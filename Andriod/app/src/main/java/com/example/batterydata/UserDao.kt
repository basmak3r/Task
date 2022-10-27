package com.example.batterydata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {
    @Query("SELECT * FROM BatteryData")
    fun getAll(): List<User>

    @Query("SELECT * FROM BatteryData WHERE timestamp BETWEEN :dateStart AND :dateEnd")
    fun getDateResult(dateStart : String, dateEnd :String):List<User>


    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}