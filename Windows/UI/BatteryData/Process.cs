using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.Data.SQLite;
using System.Globalization;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml.Linq;
using static System.Windows.Forms.VisualStyles.VisualStyleElement.TaskbarClock;


namespace BatteryData
{
    public class Process
    {
        static SQLiteConnection con = new SQLiteConnection();
        static SQLiteCommand cmd = new SQLiteCommand();



        static string file = "Data.db";
        //Define the path
        static string path = "C:\\Users\\bleju\\Desktop\\Task\\Windows\\Data\\" + file;
        static string cs = "Data Source=" + path + ";User Instance=True;";


        public static List<Discharge> BatteryCycle()
        {
            List<Discharge> list = new List<Discharge>();
            DateTime day_time = new DateTime();
            DateTime day_now = DateTime.Now;



            using var con = new SQLiteConnection(cs);


            //connection open
            con.Open();



            //Read First Data
            string stm = "SELECT timestamp from BatteryData where id=1;";
            var cmd = new SQLiteCommand(stm, con);
            SQLiteDataReader rdr = cmd.ExecuteReader();
            rdr.Read();


            if (rdr.HasRows) //if no data or service is not start yet;
            {


                day_time = rdr.GetDateTime(0);
                rdr.Close();


                //Datetime Convert MM/dd/yyyy to yyyy-MM-dd
                string SourceDate = day_time.ToString();
                string starting_date = DateTime.ParseExact(SourceDate, "MM/dd/yyyy h:mm:ss tt", null).ToString("yyyy-MM-dd HH:00:00.00");
                string ending_date = DateTime.ParseExact(SourceDate, "MM/dd/yyyy h:mm:ss tt", null).ToString("yyyy-MM-dd " + day_time.AddHours(1).ToString("HH") + ":00:00.00");



                stm = "SELECT  * From  BatteryData WHERE timestamp between @start_date  and @end_date ";
                cmd = new SQLiteCommand(stm, con);


                while (true)
                {
                    cmd.Parameters.AddWithValue("@start_date", starting_date);
                    cmd.Parameters.AddWithValue("@end_date", ending_date);


                    rdr = cmd.ExecuteReader();


                    //BatteryData Table 
                    //Id (Int32) | Charge (Float) | Timespan (Datetime) | pluggedstate ( 0 : not plugged , 1 : plugged)
                    //---------------------------------------------------------------------------------------------------

                    if (rdr.HasRows)
                    {
                        rdr.Read();
                        float initial_charge = rdr.GetFloat(1);
                        DateTime initial_time = rdr.GetDateTime(2);
                        double charge = 0.00;
                        double time = 0.00;
                        TimeSpan time_take_temp = new TimeSpan();
                        while (rdr.Read())
                        {

                            float final_charge = rdr.GetFloat(1);
                            DateTime final_time = rdr.GetDateTime(2);
                            int plugged_state = rdr.GetInt32(3);


                            if (plugged_state == 0 && initial_charge != final_charge)
                            {
                                charge += initial_charge - final_charge;
                                time_take_temp += final_time - initial_time;
                                initial_time = final_time;
                            }

                            if (plugged_state == 1)
                            {
                                initial_time = final_time;
                            }
                            initial_charge = final_charge;

                        }
                        time = time_take_temp.Minutes;
                        charge = charge * 100;
                        list.Add(new Discharge { datetime = Convert.ToDateTime(starting_date), discharge_amount = charge, discharge_time = time });
                    }
                    else
                    {
                        // Shutdown Case
                        list.Add(new Discharge { datetime = Convert.ToDateTime(starting_date), discharge_amount = 0, discharge_time = 0 });

                    }
                    rdr.Close();

                    if (day_time.AddHours(1) >= day_now)
                    {
                        break;
                    }

                    day_time = Convert.ToDateTime(starting_date).AddHours(1);
                    SourceDate = day_time.ToString();
                    starting_date = DateTime.ParseExact(SourceDate, "MM/dd/yyyy h:mm:ss tt", null).ToString("yyyy-MM-dd HH:00:00.00");
                    ending_date = DateTime.ParseExact(SourceDate, "MM/dd/yyyy h:mm:ss tt", null).ToString("yyyy-MM-dd " + day_time.AddHours(1).ToString("HH") + ":00:00.00");
                }
        }

        rdr.Close();
            con.Close();
            return list;
        }



        //Counter Addition


        public static List<Counter> CounterAdd()
        {
            List<Counter> counter = new List<Counter> { new Counter { OptimalCount = 0, BadCount = 0, SpotCount = 0 } };

            using var con = new SQLiteConnection(cs);

            string stm = "SELECT * from BatteryData;";

            con.Open();
            var cmd = new SQLiteCommand(stm, con);

            SQLiteDataReader rdr = cmd.ExecuteReader();
            DateTime time_final = new DateTime();
            DateTime time_initial = new DateTime();


            //BatteryData Table 
            //Id (Int32) | Charge (Float) | Timespan (Datetime) | pluggedstate ( 0 : not plugged , 1 : plugged)
            //---------------------------------------------------------------------------------------------------

            while (rdr.Read())
            {

                time_final = new DateTime();
                int plugged_state = rdr.GetInt32(3);
                int flag = 0;

                if (plugged_state == 1)
                {
                    flag = 1;

                    while (rdr.Read())
                    {
                        plugged_state = rdr.GetInt32(3);
                        float charge = rdr.GetFloat(1);
                        if (plugged_state == 1 && flag == 1 && charge == 1.00)
                        {
                            time_initial = rdr.GetDateTime(2);
                            time_final = rdr.GetDateTime(2);
                            flag = 2;
                        }
                        else if (flag == 2 && plugged_state == 1)
                        {
                            time_final = rdr.GetDateTime(2);
                        }

                        if (plugged_state == 0)
                            break;
                    }

                }

                if ((time_final - time_initial).Minutes >= 30 && flag == 2)
                {
                    counter[0].BadCount++;
                }
                else if ((time_final - time_initial).Minutes < 30 && flag == 2)
                {
                    counter[0].OptimalCount++;

                }
                else if (flag == 1)
                {
                    counter[0].SpotCount++;
                }
                flag = 0;

            }

            return counter;

        }
    }
}
