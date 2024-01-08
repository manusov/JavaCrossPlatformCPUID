/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000000Ch = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000000C extends ReservedFunctionCpuid
{
Cpuid8000000C()
    { setFunction( 0x8000000C ); }
}
