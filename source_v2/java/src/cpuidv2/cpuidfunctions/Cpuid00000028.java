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
Class for support CPUID Standard Function 00000028h =
Platform quality of service enforcement, for asymmetric enumeration.
This function also known as Intel Resource Director Technology (RDT)
allocation for asymmetric enumeration.
*/

package cpuidv2.cpuidfunctions;

public class Cpuid00000028 extends Cpuid00000010
{
    Cpuid00000028()
        { setFunction( 0x00000028 ); }

@Override String getLongName()
    { return "Intel RDT allocation asymmetric enumeration"; }
}
