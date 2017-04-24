//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Driver: get and interpreting CPU context management data.

package arch1.drivers.cpucontext;

import arch1.drivers.cpr.DeviceAdapter;

public class DeviceCPUcontext extends DeviceAdapter 
{
private long[] contextArray;

private String[] contextNames = new String[]
        {
        "x87 FPU/MMX, ST/MM registers" ,
        "SSE 128-bit, XMM[0-15] registers " ,
        "AVX 256-bit, YMM[0-15] registers" ,
        "BNDREG, MPX bound registers" ,
        "BNDCSR, MPX control and status registers" ,
        "AVX 512-bit predicates, K[0-7] registers" ,
        "AVX 512-bit, ZMM[0-15] registers" ,
        "AVX 512-bit, ZMM[16-31] registers" ,
        "Reserved" ,
        "PKRU, protection key state" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,

        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,

        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "LWP, Lightweight Profiling state" ,
        "XCR0 vector expansion"
        };

@Override public void setBinary(long[] x) 
    {
    contextArray = x; 
    }

@Override public int getCommandsCount()
    {
    return 1;
    }

@Override public String[] getSummaryUp()
    {
    String[] sa1 = new String[] 
        { "Feature" , "Bit" , "CPU validated" , "OS validated" };
    return sa1;        
    }

@Override public String[][] getSummaryText()
    {
    long cpuMap = contextArray[0];
    long osMap = contextArray[1];
    int n = contextNames.length;
    long mask = 1;
    String s;
    String[][] sa1 = new String[n][4];
    for ( int i=0; i<n; i++ )
        {
        sa1[i][0] = contextNames[i];
        sa1[i][1] = "" + i;
        if ( (cpuMap & mask) == 0 ) { s = "0"; } else { s = "1"; }
        sa1[i][2] = s;
        if ( (osMap & mask) == 0 )  { s = "0"; } else { s = "1"; }
        sa1[i][3] = s;
        mask <<= 1;
        }
    return sa1;    
    }

}
