/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000005h = Virtual CPUID: hypervisor multiprocessing information.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000005 extends ReservedFunctionCpuid
{
Cpuid40000005()
    { setFunction( 0x40000005 ); }
}
