/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 8000000Bh = Reserved function.

*/

package dumploader.cpuid;

class Cpuid8000000B extends ReservedFunctionCpuid
{
Cpuid8000000B()
    { setFunction( 0x8000000B ); }
}
