//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Driver: get and interpreting CPU Clock data.

package arch1.drivers.cpuclk;

import arch1.drivers.cpr.DeviceAdapter;

public class DeviceCPUCLK extends DeviceAdapter
{
private long[] cpuclkArray;

@Override public void setBinary(long[] x) 
    {
    cpuclkArray = x; 
    }

@Override public int getCommandsCount()
    {
    return 1;
    }

@Override public String[][] getSummaryText()
    {
    double frequency = cpuclkArray[0];
    frequency/=1000000.0;
    String s1 = String.format( "%.2f MHz", frequency );
    String[][] sa1 = new String[][] { { "Time Stamp Counter clock", s1 } };
    return sa1;
    }

}
