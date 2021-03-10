/*
CPUID Utility. (C)2021 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000022h = Reserved (undocumented) function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000022 extends ReservedFunctionCpuid
{
Cpuid80000022()
    { setFunction( 0x80000022 ); }
}
