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
Class for support CPUID Extended Function 80000003h = 
Processor name string [2 of 3].
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000003 extends ReservedFunctionCpuid
{
Cpuid80000003()
    { setFunction( 0x80000003 ); }

@Override String getLongName()
    { return "Processor name string [2 of 3]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80000002h" } }; }
}
