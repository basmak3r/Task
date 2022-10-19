using System.Data.SQLite;
using System.Reflection;
using System.Windows.Forms;

namespace BatteryDataReader
{
    public class Worker : BackgroundService
    {
        private readonly ILogger<Worker> _logger;

        public Worker(ILogger<Worker> logger)
        {
            _logger = logger;
        }
        public override async Task StartAsync(CancellationToken cancellationToken)
        {
            await base.StartAsync(cancellationToken);
        }
        public override async Task StopAsync(CancellationToken cancellationToken)
        {
            await base.StopAsync(cancellationToken);
        }
        public override void Dispose()
        {
        }

        static DateTime date_now;
        static SQLiteConnection con=new SQLiteConnection();
        static SQLiteCommand cmd=new SQLiteCommand();
        public static void DbDesign()
        {
            string path = "C:\\Users\\bleju\\Desktop\\Task\\Windows\\Data\\data.db";
            string cs = "Data Source="+path + ";User Instance=True";

            if (!File.Exists(path))
            {
                SQLiteConnection.CreateFile("C:\\Users\\bleju\\Desktop\\Task\\Windows\\Data\\data.db");
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

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            double battery_charge_now;
            int plugstate;
            Worker.DbDesign();
            cmd.CommandText = @"Insert into Batterydata(batterylevel,timestamp,pluggedstate) Values(@batteryper,@timestam,@plug)";
            Type t = typeof(System.Windows.Forms.PowerStatus);
            PropertyInfo[] pi = t.GetProperties();
            while (!stoppingToken.IsCancellationRequested)
            {
                object batterypercentage = pi[3].GetValue(SystemInformation.PowerStatus, null);
                object plugcharge = pi[0].GetValue(SystemInformation.PowerStatus, null);

                 battery_charge_now = Convert.ToDouble(batterypercentage.ToString());
                 plugstate = plugcharge.ToString()=="Online"?1:0;

                date_now = DateTime.Now;
                cmd.Parameters.AddWithValue("@batteryper", battery_charge_now);
                cmd.Parameters.AddWithValue("@timestam", date_now);
                cmd.Parameters.AddWithValue("@plug", plugstate);

                cmd.ExecuteNonQuery();



                await Task.Delay(60000, stoppingToken);
            }
        }
    }
}