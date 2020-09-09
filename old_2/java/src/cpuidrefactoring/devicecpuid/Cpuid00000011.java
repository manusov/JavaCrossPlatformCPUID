/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000011h = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid00000011 extends ReservedFunctionCpuid
{
Cpuid00000011()
    { setFunction( 0x00000011 ); }
}
