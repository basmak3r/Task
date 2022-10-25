using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Controls.Primitives;
using Microsoft.UI.Xaml.Data;
using Microsoft.UI.Xaml.Input;
using Microsoft.UI.Xaml.Media;
using Microsoft.UI.Xaml.Navigation;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;



namespace BatteryData
{

    public sealed partial class MainWindow : Window
    {

        public ObservableCollection<Discharge> Discharges
        {
            get; set;
        } = new();

        List<Counter> counters;

        public MainWindow()
        {
            this.InitializeComponent();
            DbDesign.DbExisting();
        }

        private void myButton_Click(object sender, RoutedEventArgs e)
        {

            myButton.Content = "Refresh";
            Discharges.Clear();
            List<Discharge> discharges= Process.BatteryCycle();

           counters = Process.CounterAdd();

           foreach (var d in discharges)
                Discharges.Add(d);
            SpotCount.Text = "Spot Count : " + counters[0].SpotCount;
            BadCount.Text = "Bad Count  : " + counters[0].BadCount;
            OptimalCount.Text = "Optimal Count : " + counters[0].OptimalCount;

        }
    }
}
