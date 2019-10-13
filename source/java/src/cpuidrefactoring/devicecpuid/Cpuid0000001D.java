/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Dh = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid0000001D extends ReservedFunctionCpuid
{
Cpuid0000001D()
    { setFunction( 0x0000001D );  }
}
