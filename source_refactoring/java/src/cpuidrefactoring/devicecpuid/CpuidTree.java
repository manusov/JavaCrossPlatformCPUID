/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Tree Information,
placeholder only.
*/

package cpuidrefactoring.devicecpuid;

class CpuidTree extends SummaryCpuid
{
@Override String getShortName() 
    { return "CPUID Tree"; }

@Override String getLongName() 
    { return "Show all supported CPUID functions as tree"; }
}
