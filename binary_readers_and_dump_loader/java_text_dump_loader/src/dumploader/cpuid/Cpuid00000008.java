/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000008h = Reserved function.

*/

package dumploader.cpuid;

class Cpuid00000008 extends ReservedFunctionCpuid
{
Cpuid00000008()
    { setFunction( 0x00000008 ); }
}
