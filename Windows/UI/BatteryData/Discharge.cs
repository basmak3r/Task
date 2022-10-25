using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BatteryData
{
    public class Discharge
    {
       public  DateTime datetime { get; set; }
        public double discharge_time { get; set; }
        public double discharge_amount { get; set; }
    }
}
