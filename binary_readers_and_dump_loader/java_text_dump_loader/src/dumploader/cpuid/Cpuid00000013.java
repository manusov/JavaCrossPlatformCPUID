/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000013h = Reserved function.

*/

package dumploader.cpuid;

class Cpuid00000013 extends ReservedFunctionCpuid
{
Cpuid00000013()
    { setFunction( 0x00000013 ); }
}
