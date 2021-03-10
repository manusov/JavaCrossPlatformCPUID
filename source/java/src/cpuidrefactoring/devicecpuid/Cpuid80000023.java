/*
CPUID Utility. (C)2021 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000023h = Reserved (undocumented) function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000023 extends ReservedFunctionCpuid
{
Cpuid80000023()
    { setFunction( 0x80000023 ); }
}
