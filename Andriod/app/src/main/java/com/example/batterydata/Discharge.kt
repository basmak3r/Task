package com.example.batterydata

import java.time.LocalDateTime


data class Discharge(
    val date_time:String,
    val discharge_amount:Double,
    val discharge_time:Long
)