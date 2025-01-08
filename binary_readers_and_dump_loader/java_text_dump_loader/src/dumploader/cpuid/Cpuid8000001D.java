/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 8000001Dh =
AMD deterministic cache parameters.

*/

package dumploader.cpuid;

class Cpuid8000001D extends Cpuid00000004
{
Cpuid8000001D()
    { setFunction( 0x8000001D ); }

@Override String getLongName()
    { return "AMD deterministic cache parameters"; }
}
