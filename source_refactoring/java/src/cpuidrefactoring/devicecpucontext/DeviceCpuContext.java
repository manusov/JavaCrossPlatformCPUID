/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for convert CPU context management information to array of text strings.
Argument is binary array of longs provided by native layer.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidrefactoring.devicecpucontext;

import cpuidrefactoring.system.Device;

public class DeviceCpuContext extends Device
{
private static final String[][] CONTEXT_UP =
    { { "Feature" , "Bit" , "CPU validated" , "OS validated" } };

private static final String[] CONTEXT_NAMES =
    {
        "x87 FPU/MMX, ST/MM registers" ,
        "SSE 128-bit, XMM[0-15] registers " ,
        "AVX 256-bit, YMM[0-15] registers" ,
        "BNDREG, MPX bound registers" ,
        "BNDCSR, MPX control and status registers" ,
        "AVX 512-bit predicates, K[0-7] registers" ,
        "AVX 512-bit, ZMM[0-15] registers" ,
        "AVX 512-bit, ZMM[16-31] registers" ,
        "Reserved" ,  // bit 8
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

private long[] contextArray;  // binary data, received from native library

@Override public void setBinary( long[] x )     // set binary data  
    { contextArray = x; }

@Override public String[][] getScreensListsUp()     // get constant up string
    { return CONTEXT_UP; }

@Override public String[][][] getScreensLists()  // get decoded result as text
    {
    long cpuMap = contextArray[0];
    long osMap = contextArray[1];
    int n = CONTEXT_NAMES.length;
    long mask = 1;
    String s1;
    String[][][] s2 = new String[1][n][4];
    for ( int i=0; i<n; i++ )
        {
        s2[0][i][0] = CONTEXT_NAMES[i];
        s2[0][i][1] = "" + i;
        s1 = ( ( cpuMap & mask ) == 0 ) ? "0" : "1";
        s2[0][i][2] = s1;
        s1 = ( ( osMap & mask ) == 0 ) ? "0" : "1";
        s2[0][i][3] = s1;
        mask <<= 1;
        }
    return s2;    
    }
}
