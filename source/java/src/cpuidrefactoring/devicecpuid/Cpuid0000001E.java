/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Eh = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid0000001E extends ReservedFunctionCpuid
{
Cpuid0000001E()
    { setFunction( 0x0000001E ); }
}
