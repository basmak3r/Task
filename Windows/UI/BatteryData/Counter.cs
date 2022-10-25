using System;
using System.Collections.Generic;
using System.Data.SQLite;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BatteryData
{

    public class Counter
    {
        public int SpotCount { get; set; }

        public int OptimalCount { get; set; }

        public int BadCount { get; set; }
    }
}
