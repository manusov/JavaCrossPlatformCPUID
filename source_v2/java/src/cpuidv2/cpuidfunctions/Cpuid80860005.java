/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Vendor Specific Function 80860005h =
Transmeta vendor-specific: Transmeta information string, part 3 of 4.
*/

package cpuidv2.cpuidfunctions;

public class Cpuid80860005 extends ReservedFunctionCpuid
{
Cpuid80860005()
    { setFunction( 0x80860005 ); }

@Override String getLongName()
    { return "Transmeta information string [3 of 4]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80860003h" } }; }
}
