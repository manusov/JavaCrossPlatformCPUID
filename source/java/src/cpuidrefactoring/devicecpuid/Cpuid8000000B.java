/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000000Bh = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid8000000B extends ReservedFunctionCpuid
{
Cpuid8000000B()
    { setFunction( 0x8000000B ); }
}
