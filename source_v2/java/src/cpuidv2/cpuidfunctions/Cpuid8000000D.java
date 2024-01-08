/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000000Dh = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000000D extends ReservedFunctionCpuid
{
Cpuid8000000D()
    { setFunction( 0x8000000D ); }
}
