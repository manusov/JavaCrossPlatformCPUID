/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000028h = Reserved (undocumented) function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000028 extends ReservedFunctionCpuid
{
Cpuid80000028()
    { setFunction( 0x80000028 ); }
}
