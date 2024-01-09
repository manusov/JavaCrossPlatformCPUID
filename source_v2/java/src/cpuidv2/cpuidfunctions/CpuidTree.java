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
Class for support CPUID Tree Information, placeholder only.
*/

package cpuidv2.cpuidfunctions;

class CpuidTree extends SummaryCpuid
{
@Override String getShortName() 
    { return "CPUID Tree"; }

@Override String getLongName() 
    { return "Show all supported CPUID functions as tree"; }
}
