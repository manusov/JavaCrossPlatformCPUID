/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Vendor Specific Function 80860006h =
Transmeta vendor-specific: Transmeta information string, part 4 of 4.

*/

package cpuidv3.servicecpuid;

class Cpuid80860006 extends ReservedFunctionCpuid
{
Cpuid80860006()
    { setFunction( 0x80860006 ); }

@Override String getLongName()
    { return "Transmeta information string [4 of 4]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80860003h" } }; }
}
