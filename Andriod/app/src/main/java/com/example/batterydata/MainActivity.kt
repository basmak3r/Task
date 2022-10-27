package com.example.batterydata

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {

    private var list_data:MutableList<Discharge>  = mutableListOf()
    private var counter_list:MutableList<Counter>  = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Start Service (Foreground and Background)
        Intent(this, BatteryReadService::class.java).also { intent ->
            startService(intent)
        }


        val badCount=findViewById<TextView>(R.id.BadCount)
        val optimalCount=findViewById<TextView>(R.id.OptimalCount)
        val spotCount=findViewById<TextView>(R.id.SpotCount)
        val button1=findViewById<Button>(R.id.button)
        val ll=findViewById<TableLayout>(R.id.table)


        //object Creation
        var cyclecalc:CycleCalculator = CycleCalculator()
        var countercalc:CounterCalculator = CounterCalculator()

        button1.setOnClickListener(){

            counter_list=countercalc.CounterCal(applicationContext)
            list_data=cyclecalc.CycleCal(applicationContext)


            badCount.setText("Bad Count : " + counter_list[0].BadCount.toString())
            optimalCount.setText("Optimal Count : " +counter_list[0].OptimalCount.toString())
            spotCount.setText("Spot Count : " +counter_list[0].SpotCount.toString())

            var count=0
            for (ls in list_data) {
                val row = TableRow(this)
                val lp: TableRow.LayoutParams =
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
                row.layoutParams = lp
                var date_time = TextView(this)
                var discharge_ = TextView(this)
                var time_ = TextView(this)
                date_time.setText(ls.date_time+"     ")
                discharge_.setText(ls.discharge_amount.toString()+"              ")
                time_.setText(ls.discharge_time.toString())
                row.addView(date_time)
                row.addView(discharge_)
                row.addView(time_)
                ll.addView(row, count)
                count++
            }
        }
    }
}
