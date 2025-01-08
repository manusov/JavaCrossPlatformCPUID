/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000004h =
Processor name string [3 of 3].

*/

package dumploader.cpuid;

class Cpuid80000004 extends ReservedFunctionCpuid
{
Cpuid80000004()
    { setFunction( 0x80000004 ); }

@Override String getLongName()
    { return "Processor name string [3 of 3]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80000002h" } }; }
}
