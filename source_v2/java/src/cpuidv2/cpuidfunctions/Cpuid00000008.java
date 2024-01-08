/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000008h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid00000008 extends ReservedFunctionCpuid
{
Cpuid00000008()
    { setFunction( 0x00000008 ); }
}
