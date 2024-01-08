/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000000Eh = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid0000000E extends ReservedFunctionCpuid
{
Cpuid0000000E()
    { setFunction( 0x0000000E ); }
}
