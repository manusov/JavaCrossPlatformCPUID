/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000000Ch = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid0000000C extends ReservedFunctionCpuid
{
Cpuid0000000C()
    { setFunction( 0x0000000C ); }
}
