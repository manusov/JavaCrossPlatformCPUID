/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for get CPU and OS context management bitmaps (capability for save
and restore CPU registers context). Also converts context management
bitmaps to array of viewable text strings. Argument is bitmaps.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidv2.services;

import cpuidv2.platforms.Detector;
import cpuidv2.CPUIDv2;

public class ServiceContext 
{
private final static int OPB_SIZE = 4096;
private final static int FN_CONTEXT = 2;
private final static String[] CONTEXT_NAMES =
    {
    "x87 FPU/MMX, ST/MM registers" ,                             // bit 0
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
    "Intel APX EGPR state (R16-R31)" ,                           // bit 19
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

public String[][] getCpuContextInfo()
    {
    long cpuMap = 0;
    long osMap = 0;
    // Get binary data use JNI call.
    Detector detector = CPUIDv2.getDetector();
    long[] opb = new long[ OPB_SIZE ];
    if ( ( detector.entryBinary( null, opb, FN_CONTEXT, OPB_SIZE ) ) != 0 )
        {
        cpuMap = opb[0];  // Load results if valid.
        osMap = opb[1];
        }
    int count = CONTEXT_NAMES.length;
    long mask = 1;
    String s;
    String[][] text = new String[count][4];
    for ( int i=0; i<count; i++ )
        {
        text[i][0] = CONTEXT_NAMES[i];
        text[i][1] = "" + i;
        s = ( ( cpuMap & mask ) == 0 ) ? "0" : "1";
        text[i][2] = s;
        s = ( ( osMap & mask ) == 0 ) ? "0" : "1";
        text[i][3] = s;
        mask <<= 1;
        }
    return text;    
    }
}
