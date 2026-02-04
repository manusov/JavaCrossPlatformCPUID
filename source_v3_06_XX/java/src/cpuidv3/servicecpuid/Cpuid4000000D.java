/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 4000000Dh = 
Reserved (undocumented) function.

*/

package cpuidv3.servicecpuid;

class Cpuid4000000D extends ReservedFunctionCpuid
{
Cpuid4000000D() { setFunction( 0x4000000D ); }
}
