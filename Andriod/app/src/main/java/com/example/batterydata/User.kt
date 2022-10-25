package com.example.batterydata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "BatteryData")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val batterylevel:Float,
    val timestamp:String,
    val pluggedstate:Int
)