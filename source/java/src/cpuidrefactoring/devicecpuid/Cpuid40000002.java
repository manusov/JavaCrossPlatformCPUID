/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000002h = Virtual CPUID: hypervisor version.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000002 extends ReservedFunctionCpuid
{
Cpuid40000002()
    { setFunction( 0x40000002 ); }
}
