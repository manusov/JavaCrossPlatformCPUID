/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Driver: get and interpreting CPU Clock data.
Note, Time Clock Counter (TSC) frequency measured by native libraries.
*/

package cpuid.drivers.cpuclk;

import cpuid.drivers.cpr.DeviceAdapter;

public class DeviceCPUCLK extends DeviceAdapter
{
private long[] cpuclkArray;  // binary data, received from native library

@Override public void setBinary(long[] x)  // set binary data 
    { cpuclkArray = x; }

@Override public int getCommandsCount()    // get supported commands count
    { return 1; }

@Override public String[][] getSummaryText()  // get decoded result as text
    {
    double frequency = cpuclkArray[0];
    frequency /= 1000000.0;
    String s1 = String.format( "%.2f MHz", frequency );
    String[][] sa1 = new String[][] { { "Time Stamp Counter clock", s1 } };
    return sa1;
    }

}
