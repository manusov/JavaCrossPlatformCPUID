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
Class for get (measure) CPU clock frequency. Also converts frequency 
value to array of viewable text strings. Argument is frequency value.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidv2.services;

import cpuidv2.platforms.Detector;
import cpuidv2.CPUIDv2;

public class ServiceClocks 
{
private final int OPB_SIZE = 4096;
private final static int FN_CLOCK = 1;

public String[][] getClocksInfo()
    {
    double frequency = 0.0;
    Detector detector = CPUIDv2.getDetector();
    long[] opb = new long[ OPB_SIZE ];
    if ( ( detector.entryBinary( null, opb, FN_CLOCK, OPB_SIZE ) ) != 0 )
        {
        frequency = opb[0];  // Load results if valid.
        }
    frequency /= 1000000.0;
    String s = String.format( "%.2f MHz", frequency );
    return new String[][] { { "Time Stamp Counter clock", s } };
    }
}
