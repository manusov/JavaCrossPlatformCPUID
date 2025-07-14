/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000028h =
Platform quality of service enforcement, for asymmetric enumeration.
This function also known as Intel Resource Director Technology (RDT)
allocation for asymmetric enumeration.

*/

package cpuidv3.servicecpuid;

public class Cpuid00000028 extends Cpuid00000010
{
Cpuid00000028()
        { setFunction( 0x00000028 ); }

@Override String getLongName()
    { return "Intel RDT allocation asymmetric enumeration"; }
}
