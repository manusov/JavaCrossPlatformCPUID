/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 8000000Dh = Reserved function.

*/

package cpuidv3.servicecpuid;

class Cpuid8000000D extends ReservedFunctionCpuid
{
Cpuid8000000D()
    { setFunction( 0x8000000D ); }
}
