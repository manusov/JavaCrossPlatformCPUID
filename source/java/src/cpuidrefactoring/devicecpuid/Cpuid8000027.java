/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000027h = Reserved (undocumented) function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000027 extends ReservedFunctionCpuid
{
Cpuid80000027()
    { setFunction( 0x80000027 ); }
}
