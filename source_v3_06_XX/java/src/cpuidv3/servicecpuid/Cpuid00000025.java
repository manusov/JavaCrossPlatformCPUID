/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000025h = Reserved function.

*/

package cpuidv3.servicecpuid;

class Cpuid00000025 extends ReservedFunctionCpuid
{
Cpuid00000025() { setFunction( 0x00000025 ); }
}
