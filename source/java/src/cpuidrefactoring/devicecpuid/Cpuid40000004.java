/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000004h = Virtual CPUID: hypervisor recommendations.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000004 extends ReservedFunctionCpuid
{
Cpuid40000004()
    { setFunction( 0x40000004 ); }
}
