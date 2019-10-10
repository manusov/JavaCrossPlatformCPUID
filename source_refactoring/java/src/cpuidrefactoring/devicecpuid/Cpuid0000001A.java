/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Ah = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid0000001A extends ReservedFunctionCpuid
{
Cpuid0000001A()
    { setFunction( 0x0000001A ); }
}
