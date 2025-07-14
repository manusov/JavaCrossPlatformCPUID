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
Class for support CPUID Standard Function 00000027h =
Platform quality of service enumeration, for asymmetric enumeration.
This function also known as Intel Resource Director Technology (RDT)
monitoring for asymmetric enumeration.
*/

package cpuidv2.cpuidfunctions;

public class Cpuid00000027 extends Cpuid0000000F
{
    Cpuid00000027()
        { setFunction( 0x00000027 ); }

@Override String getLongName()
    { return "Intel RDT monitoring asymmetric enumeration"; }
}
