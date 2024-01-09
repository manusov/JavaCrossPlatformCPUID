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
Class for support CPUID Extended Function 80000012h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000012 extends ReservedFunctionCpuid
{
Cpuid80000012()
    { setFunction( 0x80000012 ); }
}
