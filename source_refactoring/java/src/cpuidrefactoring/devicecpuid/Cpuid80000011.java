/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000011h = Reserved function.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid80000011 extends ReservedFunctionCpuid
{
Cpuid80000011()
    { setFunction( 0x80000011 ); }
}
