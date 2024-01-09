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
Class for support CPUID Extended Function 8000000Eh = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000000E extends ReservedFunctionCpuid
{
Cpuid8000000E()
    { setFunction( 0x8000000E ); }
}
