using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SQLite;
using System.IO;
using System.Drawing;
using Simplify.Windows.Forms;

namespace BatteryData
{
    public class DbDesign
    {
        static SQLiteConnection con = new SQLiteConnection();
        static SQLiteCommand cmd = new SQLiteCommand();
        public static void DbExisting()
        {

            
            string file = "Data.db";
            string dir = "C:\\ProgramData\\BatteryData";
            //Path Define
            string path = dir + "\\" + file;
            if (!Directory.Exists(dir))  
            {
                Directory.CreateDirectory(dir);
            }

            string cs = "Data Source=" + path + ";User Instance=True";


            if (!File.Exists(path))
            {
                SQLiteConnection.CreateFile(path);
            }
            con = new SQLiteConnection(cs);
            con.Open();
            cmd = new SQLiteCommand(con);
            try
            {
                cmd.CommandText = @"Select * from BatteryData";
                cmd.ExecuteNonQuery();
            }
            catch
            {
                cmd.CommandText = @"CREATE TABLE Batterydata(id INTEGER PRIMARY KEY AUTOINCREMENT,
            batterylevel float, timestamp datetime,pluggedstate int)";
                cmd.ExecuteNonQuery();
            }

        }
    }

}
