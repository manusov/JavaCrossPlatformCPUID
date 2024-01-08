/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000000Fh = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000000F extends ReservedFunctionCpuid
{
Cpuid8000000F()
    { setFunction( 0x8000000F ); }
}
