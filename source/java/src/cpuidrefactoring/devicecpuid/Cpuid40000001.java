/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000001h = Virtual CPUID: hypervisor interface information.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000001 extends ReservedFunctionCpuid
{
Cpuid40000001()
    { setFunction( 0x40000001 ); }
}
