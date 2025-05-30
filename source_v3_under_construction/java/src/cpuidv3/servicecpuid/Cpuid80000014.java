/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000014h = Reserved function.

*/

package cpuidv3.servicecpuid;

class Cpuid80000014 extends ReservedFunctionCpuid
{
Cpuid80000014()
    { setFunction( 0x80000014 ); }
}
