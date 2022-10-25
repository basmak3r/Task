package com.example.batterydata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room

import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)






//        val userDao = db.userDao()
//         val noteDatabase by lazy { UserDatabase.getDatabase(this).userDao() }
        Intent(this, BatteryReadService::class.java).also { intent ->
            startService(intent)
        }
    }
}