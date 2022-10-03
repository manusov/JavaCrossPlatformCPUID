/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000026h = Reserved (undocumented) function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000026 extends ReservedFunctionCpuid
{
Cpuid80000026()
    { setFunction( 0x80000026 ); }
}
