/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000026h = Reserved function.

*/

package cpuidv3.servicecpuid;

class Cpuid00000026 extends ReservedFunctionCpuid
{
Cpuid00000026() { setFunction( 0x00000026 ); }
}
