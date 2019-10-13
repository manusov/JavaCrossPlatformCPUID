/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000019h = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid00000019 extends ReservedFunctionCpuid
{
Cpuid00000019()
    { setFunction( 0x00000019 ); }
}
