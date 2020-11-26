/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000007h = Virtual CPUID: hypervisor logical processor information
            (Microsoft-Specific).
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000007 extends ReservedFunctionCpuid
{
Cpuid40000007()
    { setFunction( 0x40000007 ); }
}
