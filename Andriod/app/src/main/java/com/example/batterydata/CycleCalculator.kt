package com.example.batterydata

import android.app.Application
import android.content.Context
import androidx.room.Room
import java.time.LocalDateTime
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.time.Duration
import java.time.format.DateTimeFormatter


class CycleCalculator {

    private lateinit var db:AppDatabase
    private var list_data:MutableList<Discharge>  = mutableListOf()
    private fun StartandEnd(date:LocalDateTime):Pair<String,String>
    {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00.000");
        var start=date.format(formatter)
        var end=date.plusHours(1).format(formatter)
        return Pair( start,end)
    }

    private fun ConvertLocalDate(date:String):LocalDateTime
    {
        var date_convert=date
        if(date_convert.length<23)
            date_convert=date.substring(0,19)+".000"
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        var dateTime = LocalDateTime.parse(date_convert, formatter)
        return dateTime
    }

    private fun timeDifference(date_1:LocalDateTime,date_2: LocalDateTime):Long
    {
        val timediff= Duration.between(LocalDateTime.from(date_1),LocalDateTime.from(date_2))
        return timediff.toMillis()/1000;
    }

    public fun CycleCal(context:Context):MutableList<Discharge>
    {
        list_data.clear()
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        val users: List<User> = db.userDao().getAll()
        var day_time= users[0].timestamp;
        var dateTime=ConvertLocalDate(day_time)
        var(starting_date,ending_date)=StartandEnd(dateTime)
        dateTime=ConvertLocalDate(starting_date)

        while(true)
        {
            var(starting_date,ending_date)=StartandEnd(dateTime)
            val userDao = db.userDao().getDateResult(starting_date ,ending_date)
            var count=userDao.count();

            if(count>0)
            {

                var initial_charge: Float = userDao[0].batterylevel
                var initial_time =ConvertLocalDate( userDao[0].timestamp)

                var charge = 0.00
                var time :Long= 0
                var timediff:Long=0
                var i=1

                while(i<count)
                {
                    var final_charge = userDao[i].batterylevel
                    var final_time = ConvertLocalDate(userDao[i].timestamp)
                    var plugged_state = userDao[i].pluggedstate


                    if (plugged_state == 0 && initial_charge!=final_charge)
                    {
                        charge += initial_charge - final_charge;
                        timediff += timeDifference(initial_time,final_time)
                        initial_time = final_time;
                    }

                    if (plugged_state == 1)
                    {
                        initial_time = final_time;
                    }
                    initial_charge = final_charge;
                    i++
                }
                time = timediff/60;
                charge = charge;

                val list = Discharge(starting_date,charge,time);
                list_data.add(list)
            }
            else
            {
                // Shutdown Case
                val list = Discharge(starting_date,0.00,0);
                list_data.add(list);

            }

            if(dateTime.plusHours(1)>= LocalDateTime.now())
            {
                break;
            }
            dateTime=dateTime.plusHours(1)


        }
        return list_data
    }
}