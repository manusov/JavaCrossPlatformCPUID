/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000013h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid00000013 extends ReservedFunctionCpuid
{
Cpuid00000013()
    { setFunction( 0x00000013 ); }
}
