/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000027h =
Platform quality of service enumeration, for asymmetric enumeration.
This function also known as Intel Resource Director Technology (RDT)
monitoring for asymmetric enumeration.

*/

package cpuidv3.servicecpuid;

class Cpuid00000027 extends Cpuid0000000F
{
Cpuid00000027() { setFunction( 0x00000027 ); }

@Override String getLongName()
    { return "Intel RDT monitoring asymmetric enumeration"; }
}
