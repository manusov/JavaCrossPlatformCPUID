/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000009h = Virtual CPUID: hypervisor system calls information 
            (Microsoft-Specific). .
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid40000009 extends ParameterFunctionCpuid
{
Cpuid40000009()
    { setFunction( 0x40000009 ); }
}
