/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000008h = Virtual CPUID: hypervisor SVM information (Microsoft-Specific).
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000008 extends ParameterFunctionCpuid
{
Cpuid40000008()
    { setFunction( 0x40000008 ); }
}
