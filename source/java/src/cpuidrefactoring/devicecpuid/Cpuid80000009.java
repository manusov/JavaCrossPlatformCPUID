/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000009h = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000009 extends ReservedFunctionCpuid
{
Cpuid80000009()
    { setFunction( 0x80000009 ); }
}
