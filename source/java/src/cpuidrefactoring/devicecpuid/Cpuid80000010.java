/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000010h = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000010 extends ReservedFunctionCpuid
{
Cpuid80000010()
    { setFunction( 0x80000010 ); }
}
