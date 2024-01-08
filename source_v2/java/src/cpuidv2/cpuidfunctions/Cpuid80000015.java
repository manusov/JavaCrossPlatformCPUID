/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000015h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000015 extends ReservedFunctionCpuid
{
Cpuid80000015()
    { setFunction( 0x80000015 ); }
}
