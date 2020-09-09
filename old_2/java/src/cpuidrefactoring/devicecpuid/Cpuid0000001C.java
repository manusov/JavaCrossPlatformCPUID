/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Ch = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid0000001C extends ReservedFunctionCpuid
{
Cpuid0000001C()
    { setFunction( 0x0000001C ); }
}
