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
Parent class for CPUID summary and dump screen classes.
This class inherited by summary and dump screen classes.
*/

package cpuidv2.cpuidfunctions;

class SummaryCpuid extends ReservedFunctionCpuid
{
@Override String getShortName() 
    {
    return "Reserved";
    }

@Override String getLongName()
    {
    return "Reserved";
    }
}
