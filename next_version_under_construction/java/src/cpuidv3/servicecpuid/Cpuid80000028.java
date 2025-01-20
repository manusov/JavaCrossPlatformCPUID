/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000028h =
Reserved (undocumented) function.

*/

package cpuidv3.servicecpuid;

class Cpuid80000028 extends ReservedFunctionCpuid
{
Cpuid80000028()
    { setFunction( 0x80000028 ); }
}
