/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for convert CPU clock binary information to array of text strings.
Argument is binary array of longs provided by native layer.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidrefactoring.devicecpuclk;

import cpuidrefactoring.system.Device;

public class DeviceCpuClk extends Device
{
private long[] cpuclkArray;  // binary data, received from native library

@Override public void setBinary( long[] x )
    { cpuclkArray = x; }

@Override public String[][][] getScreensLists()
    {
    double frequency = cpuclkArray[0];
    frequency /= 1000000.0;
    String s = String.format( "%.2f MHz", frequency );
    return new String[][][] { { { "Time Stamp Counter clock", s } } };
    }
}
