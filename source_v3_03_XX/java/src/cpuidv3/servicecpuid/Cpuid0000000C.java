/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000000Ch = Reserved function.

*/

package cpuidv3.servicecpuid;

class Cpuid0000000C extends ReservedFunctionCpuid
{
Cpuid0000000C()
    { setFunction( 0x0000000C ); }
}
