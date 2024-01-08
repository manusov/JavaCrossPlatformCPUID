/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000000Eh = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000000E extends ReservedFunctionCpuid
{
Cpuid8000000E()
    { setFunction( 0x8000000E ); }
}
