/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000022h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid00000022 extends ReservedFunctionCpuid
{
Cpuid00000022()
    { setFunction( 0x00000022 ); }
}
