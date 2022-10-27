package com.example.batterydata

import android.content.Intent
import java.time.Period
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var db:AppDatabase
    lateinit var gridView: GridView


    private fun Date.dateToString(format: String): String {
        //simple date formatter
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

        //return the formatted date string
        return dateFormatter.format(this)
    }

    private fun StartandEnd(date:LocalDateTime):Pair<String,String>
    {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00.000");
        var start=date.format(formatter)
        var end=date.plusHours(1).format(formatter)
        return Pair( start,end)
    }

    private fun ConvertLocalDate(date:String):LocalDateTime
    {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        var dateTime = LocalDateTime.parse(date, formatter)
        return dateTime
    }

    private fun timeDifference(date_1:LocalDateTime,date_2: LocalDateTime):Long
    {
        val timediff=Duration.between(LocalDateTime.from(date_1),LocalDateTime.from(date_2))
        return timediff.toMillis()/1000;
    }

    private var list_data:MutableList<Discharge>  = mutableListOf()


    private fun CycleCal():List<Discharge>
    {
        list_data.clear()
        db = Room.databaseBuilder(
            applicationContext,
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
                var timediff=timeDifference(initial_time,initial_time)
                var i=1

                while(i<count)
                {
                    var final_charge = userDao[i].batterylevel
                    var final_time = ConvertLocalDate(userDao[i].timestamp)
                    var plugged_state = userDao[i].pluggedstate


                    if (plugged_state == 0 && initial_charge != final_charge)
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

            if(dateTime.plusHours(1)>=LocalDateTime.now())
            {
                break;
            }
            dateTime=dateTime.plusHours(1)


        }
        return list_data
    }

    private var counter_list:MutableList<Counter>  = mutableListOf()

    private fun CounterCal():List<Counter>
    {
        var count_init=Counter(0,0,0)
        counter_list.clear()
        counter_list.add(count_init)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        var time_final:LocalDateTime = LocalDateTime.now()
        var time_initial:LocalDateTime= LocalDateTime.now()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val text1=findViewById<TextView>(R.id.textView)
        val button1=findViewById<Button>(R.id.button1)

        button1.setOnClickListener(){
                text1.setText(CycleCal().toString())
                text1.setText(CounterCal().toString())


        }

        Intent(this, BatteryReadService::class.java).also { intent ->
            startService(intent)
        }

    }
}