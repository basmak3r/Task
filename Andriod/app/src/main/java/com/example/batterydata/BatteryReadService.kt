package com.example.batterydata


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class BatteryReadService : Service() {
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // do your jobs here
        registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))

        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        val chan = NotificationChannel(
            NOTIF_CHANNEL_ID, "My ForeGroundService", NotificationManager.IMPORTANCE_LOW
        )
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        startForeground(
            NOTIF_ID, NotificationCompat.Builder(
                this,
                NOTIF_CHANNEL_ID
            ) // don't forget create a notification channel first
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(true)
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    companion object {
        private const val NOTIF_ID = 1
        private const val NOTIF_CHANNEL_ID = "Channel_Id"
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                val bm = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
                val batLevel:Int = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                val batteryStatus = context!!.registerReceiver(null, ifilter)
                val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL
//                val batteryPct: Float? = batteryStatus?.let { intent ->
//                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//                    val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//                    level * 100 / scale.toFloat()
//                }
                Log.i("my_id", isCharging.toString() + batLevel.toString())
            }
        }
    }



}
