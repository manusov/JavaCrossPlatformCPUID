/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000006h = Virtual CPUID: hypervisor hardware features.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000006 extends ReservedFunctionCpuid
{
Cpuid40000006()
    { setFunction( 0x40000006 ); }
}
