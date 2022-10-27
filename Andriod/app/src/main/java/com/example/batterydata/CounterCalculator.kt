package com.example.batterydata

import android.content.Context
import androidx.room.Room
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CounterCalculator {

    private lateinit var db:AppDatabase

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


    private var counter_list:MutableList<Counter>  = mutableListOf()


    public fun CounterCal(context: Context):MutableList<Counter>
    {
        var count_init=Counter(0,0,0)
        counter_list.clear()
        counter_list.add(count_init)
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        var time_final: LocalDateTime = LocalDateTime.now()
        var time_initial: LocalDateTime = LocalDateTime.now()
        var charge:Int
        val batterydata: List<User> = db.userDao().getAll()
        val count=batterydata.count()
        var i=0
        var flag=0

        while(i<count)
        {
            var pluggedstate=batterydata[i].pluggedstate
            flag=0
            if(pluggedstate==1)
            {
                flag=1
                if( batterydata[i].batterylevel.toInt()==100)
                {
                    time_initial=ConvertLocalDate( batterydata[i].timestamp)
                    time_final=time_initial
                    flag=2
                }
                i++
                while(i<count)
                {
                    pluggedstate=batterydata[i].pluggedstate
                    charge=batterydata[i].batterylevel.toInt()
                    if(pluggedstate==1 && flag==1 && charge==100)
                    {
                        time_initial=ConvertLocalDate( batterydata[i].timestamp)
                        time_final=time_initial
                        flag=2
                    }
                    else if(flag==2 && pluggedstate==1)
                    {
                        time_final=ConvertLocalDate(batterydata[i].timestamp)
                    }
                    if(pluggedstate==0)
                        break

                    i++
                }
            }
            if(timeDifference(time_initial,time_final)/60>=30 && flag==2)
                counter_list[0].BadCount++
            else if(timeDifference(time_initial,time_final)/60<30 && flag==2 )
                counter_list[0].OptimalCount++
            else if(flag==1)
                counter_list[0].SpotCount++

            i++
        }


        return counter_list

    }
}