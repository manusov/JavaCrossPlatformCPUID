/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
4000000Ah = Virtual CPUID: hypervisor enlightened VMCS information
            (Microsoft-Specific).
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid4000000A extends ReservedFunctionCpuid
{
Cpuid4000000A()
    { setFunction( 0x4000000A ); }
}
