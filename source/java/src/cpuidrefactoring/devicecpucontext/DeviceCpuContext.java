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
        "MPX BNDREG, bound registers" ,
        "MPX BNDCSR, control and status registers" ,
        "AVX512 64-bit predicates, K[0-7] registers" ,
        "AVX 512-bit, ZMM[0-15] registers" ,
        "AVX 512-bit, ZMM[16-31] registers" ,
        "Intel processor trace state (PT), reserved for IA32_XSS" ,  // bit 8
        "PKRU, protection key state" ,                               // bit 9
        "Reserved" ,                                                 // bit 10
        "CET user state (CET_U), reserved for IA32_XSS" ,            // bit 11
        "CET supervisor state (CET_S), reserved for IA32_XSS" ,      // bit 12
        "Hardware duty cycling state (HDC), reserved for IA32_XSS" , // bit 13
        "User interrupt (UINTR), reserved for IA32_XSS" ,            // bit 14
        "Last Branch Record (LBR), reserved for IA32_XSS" ,          // bit 15
        
        "Hardware P-states (HWP), reserved for IA32_XSS" ,           // bit 16
        "Intel AMX tile configuration (XTILECFG)" ,                  // bit 17
        "Intel AMX tile data (XTILEDATA)" ,                          // bit 18
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
        "Reserved" ,                                                // bit 31

        "Reserved" ,                                                // bit 32
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
        "Reserved" ,                                                // bit 47

        "Reserved" ,                                                // bit 48
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
        "XCR0 vector expansion"                                     // bit 63
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
