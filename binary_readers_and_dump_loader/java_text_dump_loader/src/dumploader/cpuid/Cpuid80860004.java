/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Vendor Specific Function 80860004h =
Transmeta vendor-specific: Transmeta information string, part 2 of 4.

*/

package dumploader.cpuid;

class Cpuid80860004 extends ReservedFunctionCpuid
{
Cpuid80860004()
    { setFunction( 0x80860004 ); }

@Override String getLongName()
    { return "Transmeta information string [2 of 4]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80860003h" } }; }
}
