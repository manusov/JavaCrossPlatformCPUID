/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000000Eh = Reserved function.

*/

package dumploader.cpuid;

class Cpuid0000000E extends ReservedFunctionCpuid
{
Cpuid0000000E()
    { setFunction( 0x0000000E ); }
}
