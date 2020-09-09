/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000014h = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000014 extends ReservedFunctionCpuid
{
Cpuid80000014()
    { setFunction( 0x80000014 ); }
}
