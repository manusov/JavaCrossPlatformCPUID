/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000003h = Virtual CPUID: hypervisor features.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000003 extends ReservedFunctionCpuid
{
Cpuid40000003()
    { setFunction( 0x40000003 ); }
}
