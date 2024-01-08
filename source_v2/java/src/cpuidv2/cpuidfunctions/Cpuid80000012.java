/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000012h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000012 extends ReservedFunctionCpuid
{
Cpuid80000012()
    { setFunction( 0x80000012 ); }
}
