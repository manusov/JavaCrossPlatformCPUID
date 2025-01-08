/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000017h = Reserved function.

*/

package dumploader.cpuid;

class Cpuid80000017 extends ReservedFunctionCpuid
{
Cpuid80000017()
    { setFunction( 0x80000017 ); }
}
