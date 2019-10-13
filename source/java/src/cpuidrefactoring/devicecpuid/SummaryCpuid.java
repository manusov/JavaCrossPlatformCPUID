/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Parent class for CPUID summary and dump screen classes.
This class inherited by summary and dump screen classes.
*/

package cpuidrefactoring.devicecpuid;

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
